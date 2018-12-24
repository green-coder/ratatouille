# {{#str.cap}}{{project.name}}{{/str.cap}}

FIXME: description

## Usage

{{#tag.clojure}}
### Clojure

To run the Clojure project:

```shell
lein run
```

To launch the Clojure program and connect to it via a REPL:

```shell
lein repl
```

{{/tag.clojure}}
{{#tag.clojurescript}}
### Clojurescript

We use Figwheel Main for running Clojurescript during development.
More information can be found at https://figwheel.org/

To run the Clojurescript project:

```shell
lein fig:build
```

A webpage to your app will open in a browser, and your clojurescript
program will be reloaded each time you modify its source code.

{{/tag.clojurescript}}
FIXME: more explanation

## Contribute

FIXME: explain people how to contribute to this project.

## License

Copyright © {{project.year}}

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
