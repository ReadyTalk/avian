#!/bin/sh

set -e

run() {
  echo "${@}"
  ${@}
}

run make ${flags} jdk-test

for arch in i386 x86_64; do
  run make arch=${arch} ${flags} test
  run make arch=${arch} ${flags} mode=debug test
  run make arch=${arch} ${flags} process=interpret test
# bootimage and openjdk builds without openjdk-src don't work:
  if [ -z "${openjdk}" ]; then
    run make arch=${arch} ${flags} bootimage=true test
  fi
  run make arch=${arch} ${flags} tails=true continuations=true test
done
