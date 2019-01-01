# Ratatouille

[![Clojars Project](http://clojars.org/ratatouille/lein-template/latest-version.svg)](http://clojars.org/ratatouille/lein-template)

A Leiningen template which is using a small selection of the most awesome libraries available in the Clojure eco-system.

Current template's options, and the setup they provide:

- `git`: Git
- `readme`: Readme file
- `clj`: Clojure project
- `cljs`: Clojurescript project (auto-reload workflow via Figwheel Main)
- `garden`: Garden
- `reagent`: Reagent
- `re-frame`: Re-frame
- `devcards`: Devcards

When no options are provided by the user, the template prepares a plain Clojure project.

## Usage

```shell
lein new ratatouille <your-app-name> --snapshot -- [...options]
```

To see all the options:

```shell
lein new ratatouille help  --snapshot
lein new ratatouille help  --snapshot -- [...options]
```

To create a project `my-app` which uses `git` and `re-frame`:

```shell
lein new ratatouille my-app --snapshot -- git re-frame
```

## License

Copyright © 2019 Vincent Cantin

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
