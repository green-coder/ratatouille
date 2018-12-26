# Ratatouille

[![Clojars Project](http://clojars.org/ratatouille/lein-template/latest-version.svg)](http://clojars.org/ratatouille/lein-template)

A Leiningen template which is using a small selection of awesome libraries available in the Clojure eco-system.

## Usage

```shell
lein new ratatouille <your-app-name> --snapshot -- [...options]
```

To see the options:

```shell
lein new ratatouille help  --snapshot
lein new ratatouille help  --snapshot -- [...options]
```

To create a project `my-app` which uses `git` and `re-frame`:

```shell
lein new ratatouille my-app --snapshot -- git re-frame
```

## License

Copyright © 2018 Vincent Cantin

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
