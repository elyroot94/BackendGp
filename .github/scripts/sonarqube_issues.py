import requests
import json
import os
from functools import lru_cache

@lru_cache(maxsize=100)
def get_sonar_rule(rule_key):
    url = "https://sonarcloud.io/api/rules/show"
    response = requests.get(url, params={"key": rule_key},
                          headers={"Authorization": f"Bearer {os.getenv('SONAR_TOKEN')}"})
    return response.json().get("rule", {})

def fetch_issues():
    params = {
        "componentKeys": "elyroot94_BackendGp",
        "types": "CODE_SMELL,BUG",
        "severities": "BLOCKER,CRITICAL,MAJOR",
        "ps": 50
    }
    response = requests.get("https://sonarcloud.io/api/issues/search",
                          params=params,
                          headers={"Authorization": f"Bearer {os.getenv('SONAR_TOKEN')}"})
    return response.json().get("issues", [])

def process_issues():
    issues = []
    for issue in fetch_issues():
        rule = get_sonar_rule(issue["rule"])
        issues.append({
            "file": issue["component"].split(":")[-1],
            "line": issue.get("line"),
            "rule": issue["rule"],
            "severity": issue["severity"],
            "message": issue["message"],
            "sonar_solution": rule.get("htmlNote", ""),
            "rule_description": rule.get("htmlDescription", "")
        })
    with open("sonarqube_issues.json", "w") as f:
        json.dump(issues, f)

if __name__ == "__main__":
    process_issues()