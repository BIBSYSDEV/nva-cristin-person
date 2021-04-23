# nva-cristin-person

Lambda for fetching person data from the [Cristin API](https://api.cristin.no/v2/doc/index.html)

## GET person?{parameters}

| Query parameter | Description |
| ------ | ------ |
| name | name of the person, or part of the name. (Mandatory) |
| language | Preferred language for response data. Accepts 'nb', 'nn' or 'en'. (Optional) |

### Response

Returns a Json object with persons matching search query and associated metadata of the search.
Persons are listed in field 'hits' which is an array.
If no persons are found, 'hits' returns as an empty array.

### HTTP Status Codes

*   200 - Ok, returns an array of 0-5 persons.
*   400 - Bad request, returned if the parameters are invalid.
*   500 - Internal server error, returned if a problem is encountered retrieving person data
*   502 - Bad Gateway, returned if upstream get fails.

## GET person/{id}?{language_parameter}

| parameter | description |
| ------ | ------ |
| id | The unique identifier of one person (Mandatory)
| language | Preferred language for names. Accepts 'nb' or 'en'. (Optional) |

### Lookup response

Returns a Json object containing one person

### HTTP Status Codes lookup

*   200 - Ok, returns one person.
*   400 - Bad request, returned if the parameters are invalid.
*   500 - Internal server error, returned if a problem is encountered retrieving person data
*   502 - Bad Gateway, returned if upstream get fails.