workflow "Build on push" {
  on = "push"
  resolves = ["GitHub Action for Maven"]
}

action "GitHub Action for Maven" {
  uses = "xlui/action-maven-cli/jdk8@master"
  runs = "package"
  args = "clean package"
}
