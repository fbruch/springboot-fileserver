workflow "Build on push" {
  on = "push"
  resolves = ["GitHub Action for Maven"]
}

action "GitHub Action for Maven" {
  uses = "LucaFeger/action-maven-cli@9d8f23af091bd6f5f0c05c942630939b6e53ce44"
  runs = "action \"package\" {   uses = \"LucaFeger/action-maven-cli@master\"   args = \"clean install\" }"
}
