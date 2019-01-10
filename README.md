# Ratatouille

[![Clojars Project](http://clojars.org/ratatouille/lein-template/latest-version.svg)](http://clojars.org/ratatouille/lein-template)

A Leiningen template which is using a small selection of the most awesome libraries available in the Clojure eco-system.

Options available:

- `+readme`:    Has a readme.md file.
- `+git`:       Uses Git.
- `+ancient`:   Uses the lein-ancient plugin.
- `+clj:`       Uses Clojure.
- `+cljs`:      Uses Clojurescript via Figwheel Main.
- `+integrant`: Uses Integrant.
- `+http-kit`:  Is included when no tags are specified, implies some commonly used tags for a Clojure project.
- `+rum`:       Uses Rum.
- `+reagent`:   Uses Reagent.
- `+re-frame`:  Uses Re-frame.
- `+garden`:    Uses Garden.
- `+devcards`:  Uses Devcards for developing UI components in isolation from the rest of the app.

When no options are provided by the user, the template prepares a plain Clojure project.

## Usage

```shell
lein new ratatouille <project-name> [option]+
```

To display the helg message and the list of available options,
leave the options empty.

```shell
lein new ratatouille <project-name>
```

## Example

To create a project `my-app` which uses `git` and `rum`:

```shell
lein new ratatouille my-app +git +rum
```

## License

Copyright © 2019 Vincent Cantin

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
