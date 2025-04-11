# Configuration
MAX_RETRIES = 3
DELAY_BETWEEN_REQUESTS = 2
ISSUES_PER_PROVIDER = 3  # Nombre d'issues par fournisseur

SYSTEM_PROMPT = """Tu es un expert Java/Spring. Analyse ce problème SonarQube et propose :
1. Une solution implémentable
2. Une explication technique
3. Une alternative si pertinente"""

def save_suggestions(suggestions):
    output_path = Path(__file__).parent.parent / "ai_suggestions.json"
    with open(output_path, 'w') as f:
        json.dump(suggestions, f, indent=2)
    print(f"Suggestions saved to {output_path.absolute()}")

def analyze_with_openai(client, prompt):
    for _ in range(MAX_RETRIES):
        try:
            response = client.chat.completions.create(
                model="gpt-3.5-turbo",
                messages=[
                    {"role": "system", "content": SYSTEM_PROMPT},
                    {"role": "user", "content": prompt}
                ],
                temperature=0.3
            )
            return response.choices[0].message.content
        except Exception as e:
            print(f"Erreur OpenAI: {str(e)}")
            time.sleep(DELAY_BETWEEN_REQUESTS)
    return f"Échec analyse OpenAI après {MAX_RETRIES} tentatives"

def analyze_with_deepseek(issue):
    DEEPSEEK_API_URL = "https://api.deepseek.com/v1/chat/completions"
    headers = {
        "Authorization": f"Bearer {os.getenv('DEEPSEEK_API_KEY')}",
        "Content-Type": "application/json"
    }

    prompt = f"""Analyse ce problème SonarQube :
    {issue['message']}
    Fichier: {issue['file']}
    Règle: {issue['rule']}"""

    data = {
        "model": "deepseek-chat",
        "messages": [
            {"role": "system", "content": SYSTEM_PROMPT},
            {"role": "user", "content": prompt}
        ]
    }

    for _ in range(MAX_RETRIES):
        try:
            response = requests.post(DEEPSEEK_API_URL, headers=headers, json=data)
            response.raise_for_status()
            return response.json()['choices'][0]['message']['content']
        except Exception as e:
            print(f"Erreur Deepseek: {str(e)}")
            time.sleep(DELAY_BETWEEN_REQUESTS)
    return f"Échec analyse Deepseek après {MAX_RETRIES} tentatives"

def main():
    # Initialisation des clients
    openai_client = OpenAI(api_key=os.getenv("OPENAI_API_KEY")) if os.getenv("OPENAI_API_KEY") else None
    deepseek_key = os.getenv("DEEPSEEK_API_KEY")

    with open("sonarqube_issues.json") as f:
        issues = json.load(f)

    suggestions = []
    for i, issue in enumerate(issues[:6]):  # Limite à 6 issues (3 par fournisseur)
        try:
            prompt = generate_prompt(issue)

            # Alterne entre les fournisseurs
            if i % 2 == 0 and openai_client:
                result = analyze_with_openai(openai_client, prompt)
                provider = "OpenAI"
            elif deepseek_key:
                result = analyze_with_deepseek(issue)
                provider = "Deepseek"
            else:
                result = "Aucun fournisseur d'IA disponible"
                provider = "None"

            suggestions.append({
                **issue,
                "ai_suggestion": result,
                "provider": provider
            })
            print(f"Traité avec {provider} : {issue['rule']}")

        except Exception as e:
            print(f"Erreur majeure: {str(e)}")
            suggestions.append({
                **issue,
                "ai_suggestion": f"Erreur d'analyse: {str(e)}",
                "provider": "Error"
            })

    save_suggestions(suggestions)

if __name__ == "__main__":
    main()