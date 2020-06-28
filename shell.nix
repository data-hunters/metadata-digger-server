with import <nixpkgs> {};

stdenv.mkDerivation {
  name = "sbt";

  buildInputs = [
    pkgs.openjdk8_headless
    pkgs.sbt
  ];
}