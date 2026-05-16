## Serializer
    serialize ->
        1) check if object is savable ->
            Yes -> Run all addons that can handle the object. 
                  The returned object of this process will be the final result.
                -> Run all filters that can handle the result. 
                  If any filter returns false, a FilterError will be thrown. 
                  (Can be disabled to set other actions, like: Send a Log or Simply Continue). 
                -> Generally, the task consumer is called with this object. (Normally the result is returned or added to the final map).
            No -> Continue
        2) can any handler handle it ->
            Yes -> Run the handler and re-serialize the result
            No -> Continue
        3) can be serialized as map ->
            Yes -> (*SerializeAsMap) Serialize the object as a map
            No -> Throw an error (The object is not serializable)

    **SerializeAsMap** ->
        1) retrieve all fields considering the @Ignore annotations
        2) Loop through fields one by one
        3) Create a FieldSerializerTask
        4) Serialize the FieldSerializerTask until it is savable.

## Deserializer
    deserialize ->
        1) check if object is assignable ->
            Yes -> Run all addons that can handle the object.
                  The returned object of this process will be the final result.
                -> Run all filters that can handle the result. 
                  If any filter returns false, a FilterError will be thrown. 
                  (Can be disabled to set other actions, like: Send a Log or Simply Continue). 
                -> Generally, the task consumer is called with this object. (Normally the result is returned or assigned to the field).
        2) can any handler handle it ->
            Yes -> Run the handler and re-deserialize the result
            No -> Continue

## Addons ->
    The addons system allows making modular modifications to the object 
    translation process.
    In fact, an AddonsTask is passed to an addon, which contains a 
    Consumer that will later be used as the final result.
    If the result is initially valid and an addon invalidates it 
    (e.g., returns null, with null not accepted in options, or an Optional.empty), 
    then the main translator will throw an AddonError.

## Filters ->
    The filter system allows discarding certain results in a modular way. 
    Filters are always executed at the end of the process, whether it is 
    serialization or deserialization. Therefore, a filter has access to a 
    filter task containing all the information gathered from the initial process, 
    such as: executed addons, executed handlers, etc.

## Result ->
    Result is a class that implements the Consumer interface and represents 
    the result of a translator task.
    When the consumer is called filters and addons are executed on the 
    final result, makeing possible to ensure good execution of this system  
    even in translators where the outcome of a translation process is not 
    immediately guaranteed too. In addition, when the consumer is called, 
    if everything goes smoothly (meaning, by default, if all filters pass), 
    a field [private Object result;] is set with the object passed to the 
    consumer, so that it can be retrieved at any time.

# POJOs ->

## FilterTask:
    Filters are always executed at the end of any process, so they have 
    all information available.
    In fact, a filter task contains: the original task, the list of tasks 
    of the addons that were executed, and the list of handlers that were executed.
    Addons and handlers are included to greatly extend the capabilities of filters, 
    for example:
    "Discard object X only if it is null and addon Y failed to fix it".

## AddonsTask:
    Since addons are applied to the result, an addons task will contain 
    the original task (SerializerTask if it is a serialization process, 
    DeserializerTask vice versa), the list of handlers that were executed 
    for higher customization (e.g., changing the behavior of an addon based 
    on whether a handler was executed or not), and an object corresponding 
    to the unprocessed result.

## TranslatorTask:
    It is an interface that defines the foundation of a task for translating 
    an object.
    A task for any translator contains: an UUID that identifies it, the 
    translator that created it (the main process), the object to be translated, 
    the expected result type that the main translator expects, and a consumer 
    that will be called with the final result by the main process.

## SerializerTask:
    It simply implements the translator task (meaning it is a POJO containing 
    the object, the expected type... and implements the getters).
    Furthermore, being an abstract class, it declares an abstract method 
    "subprocess", which is used by handlers and addons to translate "recursively" 
    while still maintaining the core principle of each serializer (for example, 
    the iterative principle).

## DeserializerTask:
    It simply implements the translator task (meaning it is a POJO containing 
    the object, the expected type... and implements the getters).
    Furthermore, being an abstract class, it declares an abstract method 
    "subprocess", which is used by handlers and addons to translate "recursively" 
    while still maintaining the core principle of each deserializer (for example, 
    the iterative principle).