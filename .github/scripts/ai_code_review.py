from openai import OpenAI
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
    """

def main():
    client = OpenAI(api_key=os.getenv("OPENAI_API_KEY"))

    with open("sonarqube_issues.json") as f:
        issues = json.load(f)

    suggestions = []
    for issue in issues[:5]:  # Réduisez le nombre de requêtes
        try:
            response = client.chat.completions.create(
                model="gpt-3.5-turbo",  # Modèle plus accessible
                messages=[
                    {"role": "system", "content": SYSTEM_PROMPT},
                    {"role": "user", "content": generate_prompt(issue)}
                ],
                temperature=0.3,
                max_tokens=1500
            )

            suggestions.append({
                **issue,
                "ai_suggestion": response.choices[0].message.content
            })
            print(f"Traitement réussi pour {issue['rule']}")

        except Exception as e:
            print(f"Erreur sur {issue['rule']}: {str(e)}")
            suggestions.append({
                **issue,
                "ai_suggestion": f"Erreur d'analyse: {str(e)}"
            })

    save_suggestions(suggestions)

if __name__ == "__main__":
    main()