package eu.oberon.oss.tools.tsc.serz.data.tags;

import eu.oberon.oss.tools.binaryreader.BinaryDataViewer;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SERZType50Tag extends AbstractSerzTag {
    @Getter
    private int recordNameLength;
    @Getter
    private String recordName;
    @Getter
    private int recordId;
    @Getter
    private int numberOfChildren;

    public SERZType50Tag(BinaryDataViewer viewer, int recordOffset, int dataSize) {
        super(SERZTagTypes.TYPE_50, viewer, recordOffset, dataSize);
        dissect();
    }

    /* At present, the layout of this type of record looks like this:

             00 01 02 03 04 05 06 07 08 09 0A 0B 0C 0D 0E 0F ---  0123456789ABCDEF
             -----------------------------------------------      ----------------
   00000000  0A 00 00 00 63 52 65 63 6F 72 64 53 65 74 C0 C7     [    cRecordSet  ]
   00000010  AE B5 01 00 00 00                                   [                ]

    Offset  Length  Value                           Description
    00      4       0A 00 00 00                     The length of the string that follows
    04      10      63 52 65 63 6F 72 64 53 65 74   The string representing the name of the record
    14      4       C0 C7 AE B5                     The id of the record
    18      4       01 00 00 00                     The number of children contained within the container
     The corresponding xml line looks like this:
     <cRecordSet xmlns:d="http://www.kuju.com/TnT/2003/Delta" d:version="1.0" d:id="-1246836800">
 */
    private void dissect() {
        int localOffset = dataOffset;
        recordNameLength = sInt32.getValue(viewer, localOffset);
        localOffset += 4;

        recordName = cDeltaString.getValue(viewer, localOffset, recordNameLength);
        FOUND_NAMES.put(localOffset,recordName);
        localOffset += recordNameLength;

        recordId = sInt32.getValue(viewer, localOffset);
        localOffset += 4;

        numberOfChildren = sInt32.getValue(viewer, localOffset);
//        LOGGER.info("record id={}, name={}, number of children={}", recordId, recordName, numberOfChildren);
    }


}
