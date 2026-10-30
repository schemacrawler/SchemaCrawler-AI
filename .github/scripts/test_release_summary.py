import contextlib
import importlib.util
import io
import json
import os
import tempfile
import unittest
from pathlib import Path
from unittest.mock import patch


spec = importlib.util.spec_from_file_location(
    "release_summary", Path(__file__).with_name("release-summary.py")
)
summary = importlib.util.module_from_spec(spec)
spec.loader.exec_module(summary)


class ReleaseSummaryTest(unittest.TestCase):
    def test_health_version_and_state(self):
        for overrides, valid in (
            ({}, True),
            ({"_server": "SchemaCrawler AI 17.11.4-3"}, True),
            ({"_server": "SchemaCrawler 18.0.1"}, False),
            ({"in-error-state": True}, False),
            ({"in-error-state": None}, False),
            ({"transport": "stdio"}, False),
        ):
            with self.subTest(overrides=overrides), tempfile.TemporaryDirectory() as directory:
                health = {
                    "_server": "SchemaCrawler AI 18.0.1-1",
                    "in-error-state": False,
                    "transport": "http",
                    **overrides,
                }
                path = Path(directory) / "health.json"
                output = Path(directory) / "output.txt"
                path.write_text(json.dumps(health), encoding="utf-8")
                with patch.dict(os.environ, GITHUB_OUTPUT=str(output)), contextlib.redirect_stdout(io.StringIO()):
                    if valid:
                        summary.check_health(path)
                        self.assertEqual(
                            output.read_text(), f"version={health['_server'].split()[-1]}\n"
                        )
                    else:
                        with self.assertRaises(ValueError):
                            summary.check_health(path)
                        if health["_server"].startswith("SchemaCrawler AI "):
                            self.assertIn("version=", output.read_text())

    def test_latest_release(self):
        for tag, valid in (("v18.0.1-1", True), ("18.0.1-1", True), ("latest", False), ("", False)):
            with self.subTest(tag=tag):
                response = io.BytesIO(json.dumps({"tag_name": tag}).encode())
                with patch.object(summary, "urlopen", return_value=response):
                    if valid:
                        self.assertEqual(summary.latest_release(), "18.0.1-1")
                    else:
                        with self.assertRaises(ValueError):
                            summary.latest_release()

    def test_reports_versions_icons_and_failures(self):
        for status, version, expected, exit_code in (
            ("success", "17.11.4-3", ":warning: | success", 0),
            ("success", "18.0.1-1", " | success", 0),
            ("failure", "", "Unknown | failure", 0),
            ("skipped", "", "Unknown | skipped", 0),
            ("cancelled", "", "Unknown | cancelled", 0),
            ("success", "", "Unknown | Version not reported", 1),
            ("success", "bad|version", "Unknown | Version not reported", 1),
        ):
            with self.subTest(status=status, version=version):
                text, code = self.generate_report(status, version)
                self.assertEqual(code, exit_code)
                self.assertTrue(text.startswith("# Published SchemaCrawler AI Versions\n"))
                self.assertIn("| Not latest | Test result |", text)
                self.assertIn(expected, text)
                self.assertNotIn("Maven", text)

    def test_failed_latest_lookup_still_writes_report(self):
        text, code = self.generate_report("success", "18.0.1-1", baseline_error=True)
        self.assertEqual(code, 1)
        self.assertIn("Latest SchemaCrawler AI release: **Unknown**", text)
        self.assertIn("| 18.0.1-1 | Unknown | success |", text)
        self.assertIn("Latest SchemaCrawler AI release lookup failed", text)

    def generate_report(self, status, version, baseline_error=False):
        results = {
            "test-docker": {"result": "success", "outputs": {"version": "18.0.1-1"}},
            "test-mcp": {"result": status, "outputs": {"version": version}},
        }
        with tempfile.TemporaryDirectory() as directory:
            path = Path(directory) / "summary.md"
            env = {"PACKAGE_RESULTS": json.dumps(results), "GITHUB_STEP_SUMMARY": str(path)}
            with (
                patch.dict(os.environ, env),
                patch.object(summary, "latest_release") as latest,
                contextlib.redirect_stderr(io.StringIO()),
            ):
                if baseline_error:
                    latest.side_effect = OSError("GitHub unavailable")
                else:
                    latest.return_value = "18.0.1-1"
                code = summary.report()
            return path.read_text(encoding="utf-8"), code


if __name__ == "__main__":
    unittest.main()
