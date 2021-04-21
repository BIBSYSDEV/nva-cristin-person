# nva-cristin-person

Lambda for fetching person data from the [Cristin API](https://api.cristin.no/v2/doc/index.html)

## GET person?{parameters}

| Query parameter | Description |
| ------ | ------ |
| name | Name, or part of the name of the person to search for. Accepts letters, digits, dash and whitespace. (Mandatory) |

## GET person/{id}

| Query parameter | Description |
| ------ | ------ |
| id | Id of person to fetch.

### Response

Returns a JSON array containing a number of persons, or an empty JSON array if no persons are found.

#### HTTP Status Codes

*  200 - Ok, returns 0-10 persons.
*  400 - Bad request, returned if the parameters are invalid.
*  500 - Internal server error, returned if a problem is encountered retrieving person data.