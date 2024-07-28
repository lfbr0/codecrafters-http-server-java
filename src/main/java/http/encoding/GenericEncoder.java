package http.encoding;

import java.io.IOException;

public interface GenericEncoder {
    StringBuffer encode(StringBuffer body);
}
