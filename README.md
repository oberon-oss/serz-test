## Description of currently known tags:

### SERZ tag type 0x50

This type of record appears to be a container for other records. Sofar, no records have been found of this type, that, when represented in it's XML, show that
the tags have any attributes, apart from the ID attribute. All binary forms has four bytes that contain the id, but the ones that have 00 00 00 00 will show up
in XML as a tag that has no id attributes

Here is an example of a type 0x50 tag:

| Offset | Length | Value                         | Description                                                         |
|--------|--------|-------------------------------|---------------------------------------------------------------------|
| 00     | 4      | FF 50 FF FF                   | Signature identifier for this tag type                              |
| 04     | 4      | 0A 00 00 00                   | The length of the string that follows (10 bytes)                    |
| 08     | 10     | 63 52 65 63 6F 72 64 53 65 74 | The string representing the name of the record                      |
| 28     | 4      | C0 C7 AE B5                   | The id of the record (sometimes only 00 00 00 00, indicating NO ID) |
| 32     | 4      | 01 00 00 00                   | The number of children contained within the container (1)           |

The corresponding xml line looks like this:

    <cRecordSet xmlns:d="http://www.kuju.com/TnT/2003/Delta" d:version="1.0" d:id="-1246836800">

## Description of currently known attributes:

###               

| Name in .bin | XML attribute name    | Type                    | lenth(bytes) | Java type   | Description                      |
|--------------|-----------------------|-------------------------|--------------|-------------|----------------------------------|
| bool         | d:type="bool"         | boolean                 | 1            | boolean     | Either true (1) or false (0)     |
| sFloat32     | d:type="sFloat32"     | floating point tyoe     | 4            | float/Float | Single-precision 32-bit IEEE 754 |
| sUInt64      | d:type="sUInt64"      | unsigned 64 bit integer | 8            | BigInteger  |                                  |
| sInt32       | d:type="sInt32"       | signed 32 bit integer   | 4            | int/Integer |                                  |
| sUInt32      | d:type="sUInt32"      | unsigned 32 bit integer | 4            | Long        |                                  |
| cDeltaString | d:type="cDeltaString" | Variable length string  | Variable     | String      |                                  |
|              |                       |                         |              |             |                                  |
|              |                       |                         |              |             |                                  |
|              |                       |                         |              |             |                                  |

