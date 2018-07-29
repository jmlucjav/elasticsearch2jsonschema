//groovy elasticsearch2jsonschema.groovy ./mappings-employee.json employee
import groovy.json.*

if (args.length!=2){
    println "Usage: elasticsearch2jsonschema.groovy path_to_mapping pojo_name"
    System.exit(1)
}

def jsonSlurper = new JsonSlurper()
File file = new File(args[0])
def map = [:]
map.type='object'

def promap = [:]
def defmap = [:] 
Map props = (Map)(jsonSlurper.parse(file).properties)
props.each { key, value ->
    processObject (promap, defmap, key, value)
}

map.definitions=defmap
map.properties=promap

String outf = args[1] 
println "json output at $outf"
def json = new groovy.json.JsonBuilder()
json map
new File(outf).withWriter { out ->
  out.println JsonOutput.prettyPrint(json.toString())
}

System.exit(0)

void processObject(amap, defmap, key, val){
    if (val.type){
        //simple
        amap.put(key, getField(val.type))
    }else{
        //handle object
        amap.put(key, getDefinition(defmap, key, val.properties))
    }
}

Map getField(type){
    def result=type
    def rmap=[:]
    switch (type) {
        case 'text':
        case 'keyword':
            result = 'string'
            break
        case 'long':
            result = 'integer'
            break
        case 'date':
            result = 'string'
            rmap.format="date-time"
            break
    }   
    rmap.put('type',result)
    return rmap
}

// see https://spacetelescope.github.io/understanding-json-schema/structuring.html
Map getDefinition(defmap, key, properties){
    def rmap=[:]
    //handle arrays
    def containedObj = getContainedObject(key)
    if (containedObj){
        //container ref
        def itemrefs = ['$ref':"#/definitions/$containedObj"]
        def dmap = ['type':'array', 'items':itemrefs]
        defmap.put(key,dmap)
        //contained ref
        def cmap = ['type':'object']
        def cprop = [:] 
        properties.each { fkey, value ->
            // println "$fkey $value"
            processObject (cprop, defmap, fkey, value)
        }
        cmap.properties=cprop
        defmap.put(containedObj,cmap)
    } else{
        def dmap = ['type':'object']
        def defprop = [:] 
        properties.each { fkey, value ->
            // println "$fkey $value"
            processObject (defprop, defmap, fkey, value)
        }
        dmap.properties=defprop
        defmap.put(key,dmap)
    }
    rmap.put('$ref',"#/definitions/$key")
    return rmap
}

//return singular name if passed a plural (to detect arrays)
String getContainedObject(name){
    if (name.endsWith('s')){
        if (name.endsWith('ss') || name.endsWith('Items')){
            return ""
        }else{
            return name[0..name.size()-2]
        }
    }
    return ""
}
