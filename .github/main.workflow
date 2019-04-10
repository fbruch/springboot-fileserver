workflow "Build on push" {
  on = "push"
  resolves = ["package"]
}

action "package" {
  uses = "xlui/action-maven-cli/jdk8@master"
  runs = "package"
  args = "clean package"
}
