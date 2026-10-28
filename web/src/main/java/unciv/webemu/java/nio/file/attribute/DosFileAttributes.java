package unciv.webemu.java.nio.file.attribute;

import java.nio.file.attribute.BasicFileAttributes;

/** Only referenced by an error message helper; browsers don't have DOS attributes. */
public interface DosFileAttributes extends BasicFileAttributes {
    boolean isReadOnly();
    boolean isHidden();
    boolean isArchive();
    boolean isSystem();
}
