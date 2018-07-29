# elasticsearch2jsonschema: generate JSON Schema from Elasticsearch mappings

Description
---------------

[jsonschema2pojo](http://www.jsonschema2pojo.org/) is my preferred tool to generate java POJOs from json files. Most of the times I already have some json samples I can feed to jsonschema2pojo, but sometimes I do not have that. What I have is the Elasticsearch mappings, would it be some way to feed those to jsonschema2pojo? Maybe there is...another thing you can feed is JSON Schema, and Elasticsearch mappings format looks like JSON Schema right?

Unfortunately, it is far from it. But it was not hard to hack some Groovy script to generate valid JSON Schema from Elasticsearch mappings. As a bonus, now you can go from Elasticsearch mappings to any other tool that accepts JSON Schema as input.

The only thing that should the changed for real usage is this: getContainedObject method is used to detect arrays. Customize it for your own use case. Other than that, it should work beautifully, as it did for me.


