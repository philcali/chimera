# Chimera

The Chimera emerged in Homer's classic literature _[Iliad][1]_. The best had
multiple heads. The purpose of this library is to give Scala objects the ability
to transform into other generic data types without having to annotate your
objects in any ridiculous way.

## Example

``` scala
import com.github.philcali.chimera.Reflector._

case class Interest(name: String)
case class Person(
  name: String,
  interests: List[Interest],
  spouse: Option[Person] = None
)

val interests = List(Interest("Book"))
val wife = Person("Anna Cali", interests)
val philip = Person("Philip Cali", Interest("Coding") :: interests, Some(wife))

// Person's chimera must look into the JSON mirror
val jsonPhilip = philip.toChimera.as[JSONObject]

println(jsonPhilip)
/**
 Note: This output was hand-edited for display purposes
{
  "name" : "Philip Cali",
  "interests" : [
    {"name" : "Coding"},
    {"name" : "Books" }
  ],
  "spouse" : {
    "name" : "Anna Cali",
    "interests" : [
      {"name" : "Books"}
    ]
  }
}
*/

// JSON's chimera must look into the Person mirror
println(philip == jsonPhilip.toChimera.as[Person]) // true
```

## Chimeras and Mirrors

Here's the basic idea: A `Chimera` object is a simple internal object meant to
facilitate conversion between generic data types. There's really nothing special
about a `Chimera` other than the fact that it uses Scala 2.10 `Dynamic` to
perform object traversal.

The `Mirror`s make the `Chimera` appear a certain way using reflection. You
don't have to annotate your objects in any way... just import the library.

Other than the common collections, primitives, and case classes, the library
doesn't support any other mirrors. Building a custom mirror is easy enough,
but you have to make sure it is generic enough across data types. For example:
you might define a mirror for a datatime based on a unix timestamp or a string.
This is entirely within your control.

## Known Issues

- Thread safety issues via runtime reflection
- The extension point for `Mirror`s is pretty terrible
- The need for `CollectionChimera` is dwindling
- The xml module doesn't make sense

[1]: http://en.wikipedia.org/wiki/Iliad
