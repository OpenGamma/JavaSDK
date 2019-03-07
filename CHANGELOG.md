Change Log
==========

Changes to the SDK, listed in the style of [keepachangelog](https://keepachangelog.com/en/1.0.0/).

## Version 3.3.0

### Added

* Return bucketed delta/gamma by curve at the portfolio level

### Fixed

* Fix what-if result object to return combined and delta margin in correct fields


## Version 3.2.0 - 2019-02-15

### Added

* Support for CDSClear Margin request

## Version 3.1.3 - 2019-02-07

### Task

* Updated dependencies


## Version 3.1.2 - 2019-01-21

### Added

* Support for user-requested network retries, in case of failures


## Version 3.1.2 - 2018-12-18

### Fixed

* Periods were not being sent correctly to the server in some cases


## Version 3.1.2 - 2018-10-11

### Added

* Major enhancement to the API for new functionality
* All users get a standardized breakdown of the margin number
* Callers with the right permissions can now request margin detail and trade valuations
* The existing `MarginCalcRequestType` has been replaced with `Set<MarginCalcType>`
 (but with backwards compatibility retained via deprecations)


## Version 3.0.1 - 2018-04-17

### Fixed

* The asynchronous margin calculation methods were rewritten
* If an interrupt happens while blocked, the interrupt flag will now be set


## Version 3.0.0- 2018-02-08

### Changed

* The SDK packages are restructured to a design suitable for the long term
