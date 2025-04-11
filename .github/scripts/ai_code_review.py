import openai
import json
import os
from pathlib import Path

SYSTEM_PROMPT = """Tu es un expert Java/Spring assistant des développeurs.
Tu DOIS :
1. Combiner les solutions SonarQube avec les bonnes pratiques modernes
2. Proposer du code valide pour Spring Boot 3+
3. Expliquer chaque changement clairement
4. Donner des alternatives si pertinent"""

def save_suggestions(suggestions):
    output_path = Path(__file__).parent.parent / "ai_suggestions.json"
    with open(output_path, 'w') as f:
        json.dump(suggestions, f, indent=2)
    print(f"Suggestions saved to {output_path.absolute()}")

def generate_prompt(issue):
    return f"""
    **Problème**:
    {issue['message']}

    **Fichier**: {issue['file']}:{issue.get('line', 'N/A')}
    **Règle**: {issue['rule']} ({issue['severity']})

    **Solution SonarQube**:
    {issue.get('sonar_solution', 'Non fournie')}

    **Demande**:
    1. Propose une solution implémentable
    2. Explique pourquoi ça résout le problème
    3. Donne une alternative si applicable

    **Format de réponse**:
    ```markdown
    ### Solution recommandée
    [Détails]

    ```java
    [Code]
    ```

    ### Explication
    [Justification technique]
    ```
    """

def main():
    openai.api_key = os.getenv("OPENAI_API_KEY")

    with open("sonarqube_issues.json") as f:
        issues = json.load(f)

    suggestions = []
    for issue in issues[:10]:  # Limite pour contrôle des coûts
        response = openai.ChatCompletion.create(
            model="gpt-4",
            messages=[
                {"role": "system", "content": SYSTEM_PROMPT},
                {"role": "user", "content": generate_prompt(issue)}
            ],
            temperature=0.3
        )
        suggestions.append({
            **issue,
            "ai_suggestion": response.choices[0].message.content
        })

    with open("ai_suggestions.json", "w") as f:
        json.dump(suggestions, f)

if __name__ == "__main__":
    main()
    save_suggestions(suggestions)