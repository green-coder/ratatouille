# Ratatouille

[![Clojars Project](http://clojars.org/ratatouille/lein-template/latest-version.svg)](http://clojars.org/ratatouille/lein-template)

A Leiningen template which is using a small selection of the most awesome libraries available in the Clojure eco-system.

Options available:

- `+readme`:    Has a readme.md file.
- `+git`:       Uses Git, makes an initial commit.
- `+ancient`:   Uses the lein-ancient plugin.
- `+clj`:       Uses Clojure.
- `+cljs`:      Uses Clojurescript via Figwheel Main.
- `+integrant`: Uses Integrant.
- `+http-kit`:  Uses Http-kit.
- `+reitit`:    Uses Reitit.
- `+rum`:       Uses Rum.
- `+reagent`:   Uses Reagent.
- `+re-frame`:  Uses Re-frame.
- `+garden`:    Uses Garden, dynamically injects CSS from front end code.
- `+devcards`:  Uses Devcards for developing UI components in isolation from the rest of the app.

Depending on what the user chooses, some options will be implicitly included. For instance:

- using `+rum` will include `+cljs`,
- using `+http-kit` will include `+integrant` and `+clj`.

## Usage

> **Note:** To use the snapshot version instead of a stable release, insert
` --snapshot -- ` between the project name and the options.

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
