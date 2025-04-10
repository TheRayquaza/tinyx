# shell.nix
{ pkgs ? import <nixpkgs> {} }:

pkgs.mkShell {
  buildInputs = [
    # Python environment
    pkgs.python312
    pkgs.python312Packages.pip
    pkgs.python312Packages.setuptools
    pkgs.python312Packages.pre-commit-hooks
    pkgs.pre-commit

    # Java + Maven
    pkgs.openjdk17
    pkgs.maven
  ];

  shellHook = ''
    if [ ! -d ".venv" ]; then
      python3 -m venv .venv
      . .venv/bin/activate
    fi
    # Activate pre-commit environment if inside virtualenv
    if [ -f ".venv/bin/activate" ]; then
      source .venv/bin/activate
    fi
  '';
}
