# Contributing to ARLAS-tagger

ARLAS-tagger is an open source project and there are many ways to contribute.

## Bug reports

If you think you have found a bug in ARLAS-tagger, first make sure that it has not been already addressed in our
[issues list](https://github.com/gisaia/ARLAS-tagger/issues).

If not, provide as much information as you can to help us reproduce your bug :

- ARLAS-tagger version and configuration file used
- Kafka version used
- requests and data payloads to reproduce the bug

Keep in mind that we will fix your problem faster if we can easily reproduce it.

## Feature requests

If you think ARLAS-tagger lacks a feature, do not hesitate to open an issue on our
[issues list](https://github.com/gisaia/ARLAS-tagger/issues) on GitHub which describes what you need, why you need it,
and how it should work.

## Contributing code and documentation changes

If you want to submit a bugfix or a feature implementation, first find or open an issue about it on our
[issues list](https://github.com/gisaia/ARLAS-tagger/issues)

#### Prerequisites

ARLAS-tagger runs with JDK8 and is built/packaged with maven 3.
Codebase follows [IntelliJ](https://www.jetbrains.com/idea/) default formatting rules.

#### Fork and clone the repository

You will need to fork the main ARLAS-tagger repository and clone it to your local machine. See
[github help page](https://help.github.com/articles/fork-a-repo) for help.

#### Submitting your changes

When your code is ready, you will have to :

- rebase your repository.
- run ./scripts/tests-integration.sh which should exit with a `0` status.
- update documentation in `docs/` and tests in `src/test` if relevant
- [submit a pull request](https://help.github.com/articles/using-pull-requests) with a proper title and a mention to
the corresponding issue (eg "fix #1234").
- never force push your branch after submitting, if you need to sync with official repository, you should better merge
master into your branch.