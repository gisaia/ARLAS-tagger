# Change Log

## [v11.6.6](https://github.com/gisaia/ARLAS-tagger/tree/v11.6.6) (2019-11-18)

[Full Changelog](https://github.com/gisaia/ARLAS-tagger/compare/v11.6.5...v11.6.6)

**Fixed bugs:**

- \[Tagger\] Rollback breaking change on status endpoint [\#52](https://github.com/gisaia/ARLAS-tagger/issues/52)
- Rollback breaking change on status endpoint \(fix \#52\) [\#53](https://github.com/gisaia/ARLAS-tagger/pull/53) ([alainbodiguel](https://github.com/alainbodiguel))

## [v11.6.5](https://github.com/gisaia/ARLAS-tagger/tree/v11.6.5) (2019-11-18)

[Full Changelog](https://github.com/gisaia/ARLAS-tagger/compare/v11.6.4...v11.6.5)

**New stuff:**

- \[Tagger\] Use slicing for Untag operation [\#50](https://github.com/gisaia/ARLAS-tagger/issues/50)
- \[Tagger\] Endpoint for list of tagging jobs [\#33](https://github.com/gisaia/ARLAS-tagger/issues/33)
- Use slicing for Untag operation \(fix \#50\) [\#51](https://github.com/gisaia/ARLAS-tagger/pull/51) ([alainbodiguel](https://github.com/alainbodiguel))
- Endpoint for list of tagging jobs \(fix \#33\) [\#49](https://github.com/gisaia/ARLAS-tagger/pull/49) [[API](https://github.com/gisaia/ARLAS-tagger/labels/API)] ([alainbodiguel](https://github.com/alainbodiguel))
- Fix minor documentation issue [\#48](https://github.com/gisaia/ARLAS-tagger/pull/48) [[documentation](https://github.com/gisaia/ARLAS-tagger/labels/documentation)] ([elouanKeryell-Even](https://github.com/elouanKeryell-Even))

**Miscellaneous:**

- \[Tagger\] Generate API documentation [\#46](https://github.com/gisaia/ARLAS-tagger/issues/46) [[documentation](https://github.com/gisaia/ARLAS-tagger/labels/documentation)]

## [v11.6.4](https://github.com/gisaia/ARLAS-tagger/tree/v11.6.4) (2019-10-24)

[Full Changelog](https://github.com/gisaia/ARLAS-tagger/compare/v11.6.3...v11.6.4)

**New stuff:**

- \[Tagger\] Add support for ES x-pack [\#32](https://github.com/gisaia/ARLAS-tagger/issues/32)
- Support multiple execute consumers [\#42](https://github.com/gisaia/ARLAS-tagger/pull/42) ([alainbodiguel](https://github.com/alainbodiguel))
- Feature/x pack [\#37](https://github.com/gisaia/ARLAS-tagger/pull/37) ([alainbodiguel](https://github.com/alainbodiguel))
- Refactor TaggingStatus  [\#43](https://github.com/gisaia/ARLAS-tagger/pull/43) ([alainbodiguel](https://github.com/alainbodiguel))

**Fixed bugs:**

- Remove DefaultHealthCheck as dropwizard provides, by default, a deadlock healthcheck  [\#15](https://github.com/gisaia/ARLAS-tagger/issues/15)

**Miscellaneous:**

- Aggregate the different docker-compose files into one [\#9](https://github.com/gisaia/ARLAS-tagger/issues/9)
- Add configuration by environment variable [\#28](https://github.com/gisaia/ARLAS-tagger/issues/28)

## [v11.6.3](https://github.com/gisaia/ARLAS-tagger/tree/v11.6.3) (2019-10-14)

[Full Changelog](https://github.com/gisaia/ARLAS-tagger/compare/v11.6.2...v11.6.3)

**New stuff:**

- \[Tagger\] Add tracing for process time [\#35](https://github.com/gisaia/ARLAS-tagger/issues/35)
- \[Tagger\] Improve Kafka reliability [\#30](https://github.com/gisaia/ARLAS-tagger/issues/30) [[conf](https://github.com/gisaia/ARLAS-tagger/labels/conf)]
- Add tracing \(process time\) [\#36](https://github.com/gisaia/ARLAS-tagger/pull/36) ([alainbodiguel](https://github.com/alainbodiguel))
- Improve Kafka reliability  [\#31](https://github.com/gisaia/ARLAS-tagger/pull/31) [[conf](https://github.com/gisaia/ARLAS-tagger/labels/conf)] ([alainbodiguel](https://github.com/alainbodiguel))

## [v11.6.2](https://github.com/gisaia/ARLAS-tagger/tree/v11.6.2) (2019-10-03)

[Full Changelog](https://github.com/gisaia/ARLAS-tagger/compare/v11.6.1...v11.6.2)

**New stuff:**

- Add logging in conf + set progress to 100 when no propagation is possible [\#29](https://github.com/gisaia/ARLAS-tagger/pull/29) ([MohamedHamouGisaia](https://github.com/MohamedHamouGisaia))

## [v11.6.1](https://github.com/gisaia/ARLAS-tagger/tree/v11.6.1) (2019-09-20)

[Full Changelog](https://github.com/gisaia/ARLAS-tagger/compare/v11.6.0...v11.6.1)

**New stuff:**

- Add in the configuration file of the ARLAS-tagger a field for providing kafka connection properties [\#14](https://github.com/gisaia/ARLAS-tagger/issues/14)
- Add HTTPS in the swagger definition [\#22](https://github.com/gisaia/ARLAS-tagger/pull/22) ([alainbodiguel](https://github.com/alainbodiguel))
- Kafka extra properties [\#21](https://github.com/gisaia/ARLAS-tagger/pull/21) ([alainbodiguel](https://github.com/alainbodiguel))
- Add Doc generation for python library [\#26](https://github.com/gisaia/ARLAS-tagger/pull/26) ([MohamedHamouGisaia](https://github.com/MohamedHamouGisaia))
- Add script \(mkDocs.sh\) that generates documentation [\#25](https://github.com/gisaia/ARLAS-tagger/pull/25) [[documentation](https://github.com/gisaia/ARLAS-tagger/labels/documentation)] ([MohamedHamouGisaia](https://github.com/MohamedHamouGisaia))

**Fixed bugs:**

- The entry point in the arlas-tagger container is doubled and ends up in a shell [\#20](https://github.com/gisaia/ARLAS-tagger/issues/20)
- Version of arlas-tagger-api is not correctly set in the release script [\#16](https://github.com/gisaia/ARLAS-tagger/issues/16)

## [v11.6.0](https://github.com/gisaia/ARLAS-tagger/tree/v11.6.0) (2019-09-06)

[Full Changelog](https://github.com/gisaia/ARLAS-tagger/compare/a3bb646c7de59a44748911178a1e51d66758349d...v11.6.0)

**New stuff:**

- Migrate arlas-tagger source code from ARLAS-server project [\#2](https://github.com/gisaia/ARLAS-tagger/issues/2)
- Add CI that run IT [\#8](https://github.com/gisaia/ARLAS-tagger/pull/8) ([MohamedHamouGisaia](https://github.com/MohamedHamouGisaia))
- Start the stack with docker [\#7](https://github.com/gisaia/ARLAS-tagger/pull/7) ([MohamedHamouGisaia](https://github.com/MohamedHamouGisaia))
- Implement arlas-tagger-tests [\#6](https://github.com/gisaia/ARLAS-tagger/pull/6) ([MohamedHamouGisaia](https://github.com/MohamedHamouGisaia))
- Implement arlas-tagger-rest [\#5](https://github.com/gisaia/ARLAS-tagger/pull/5) [[API](https://github.com/gisaia/ARLAS-tagger/labels/API)] ([MohamedHamouGisaia](https://github.com/MohamedHamouGisaia))
- Implement arlas-tagger-core [\#4](https://github.com/gisaia/ARLAS-tagger/pull/4) ([MohamedHamouGisaia](https://github.com/MohamedHamouGisaia))
- Implement arlas-tagger app [\#3](https://github.com/gisaia/ARLAS-tagger/pull/3) ([MohamedHamouGisaia](https://github.com/MohamedHamouGisaia))



\* *This Change Log was automatically generated by [github_changelog_generator](https://github.com/skywinder/Github-Changelog-Generator)*