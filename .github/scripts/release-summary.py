import argparse
import json
import os
import re
import sys
from pathlib import Path
from urllib.request import Request, urlopen


LATEST_RELEASE_URL = "https://api.github.com/repos/schemacrawler/SchemaCrawler-AI/releases/latest"
VERSION_PATTERN = r"\d+(?:\.\d+){2,}(?:[-+][0-9A-Za-z.-]+)?"
IMAGES = {
    "test-docker": "schemacrawler/schemacrawler-ai:latest",
    "test-mcp": "mcp/schemacrawler-ai:latest",
}


def cell(value):
    return str(value).replace("|", r"\|").replace("\r", "").replace("\n", " ")


def latest_release():
    headers = {"Accept": "application/vnd.github+json", "User-Agent": "SchemaCrawler-AI-release-test"}
    if os.environ.get("GH_TOKEN"):
        headers["Authorization"] = f"Bearer {os.environ['GH_TOKEN']}"
    with urlopen(Request(LATEST_RELEASE_URL, headers=headers), timeout=30) as response:
        release = json.load(response)
    version = release.get("tag_name", "").removeprefix("v")
    if not re.fullmatch(VERSION_PATTERN, version):
        raise ValueError(f"Invalid or missing latest SchemaCrawler AI release: {version!r}")
    return version


def check_health(path):
    health = json.loads(Path(path).read_text(encoding="utf-8"))
    print(json.dumps(health, indent=2))
    match = re.fullmatch(rf"SchemaCrawler AI ({VERSION_PATTERN})", health.get("_server", ""))
    if not match:
        raise ValueError("Health endpoint did not return a SchemaCrawler AI version")
    with Path(os.environ["GITHUB_OUTPUT"]).open("a", encoding="utf-8") as output:
        output.write(f"version={match[1]}\n")
    if health.get("in-error-state") is not False:
        raise ValueError("MCP server is in an error state or did not report its state")
    if health.get("transport") != "http":
        raise ValueError("MCP server is not using HTTP transport")


def report():
    results = json.loads(os.environ["PACKAGE_RESULTS"])
    latest = None
    errors = []
    try:
        latest = latest_release()
    except (OSError, ValueError) as error:
        errors.append(f"Latest SchemaCrawler AI release lookup failed: {error}")
    lines = [
        "# Published SchemaCrawler AI Versions",
        "",
        f"Latest SchemaCrawler AI release: **{cell(latest or 'Unknown')}** "
        "([GitHub](https://github.com/schemacrawler/SchemaCrawler-AI/releases/latest)).",
        "",
        ":warning: = version differs from the latest SchemaCrawler AI release. "
        "Unknown = version or latest-release baseline unavailable.",
        "",
        "Versions are read from each running MCP server's `/health` endpoint.",
        "",
        "| Docker image | SchemaCrawler AI version | Not latest | Test result |",
        "| --- | --- | --- | --- |",
    ]
    for job, image in IMAGES.items():
        result = results[job]
        version = result["outputs"].get("version", "")
        status = result["result"]
        if version and not re.fullmatch(VERSION_PATTERN, version):
            errors.append(f"{image}: invalid reported version {version!r}")
            version = ""
        if not version and status == "success":
            errors.append(f"{image}: successful test did not report a SchemaCrawler AI version")
            status = "Version not reported"
        indicator = "Unknown" if not version or not latest else ":warning:" if version != latest else ""
        lines.append(
            f"| {image} | {cell(version or 'Not reported')} | {indicator} | {cell(status)} |"
        )
    if errors:
        lines.extend(["", "## Reporting errors", "", *(f"- {cell(error)}" for error in errors)])
    with Path(os.environ["GITHUB_STEP_SUMMARY"]).open("a", encoding="utf-8") as summary:
        summary.write("\n".join(lines) + "\n")
    for error in errors:
        print(error, file=sys.stderr)
    return 1 if errors else 0


def main():
    parser = argparse.ArgumentParser()
    commands = parser.add_subparsers(dest="command", required=True)
    commands.add_parser("check-health").add_argument("path")
    commands.add_parser("report")
    args = parser.parse_args()
    if args.command == "check-health":
        check_health(args.path)
        return 0
    return report()


if __name__ == "__main__":
    sys.exit(main())
