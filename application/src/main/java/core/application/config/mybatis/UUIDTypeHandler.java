package core.application.config.mybatis;

import java.nio.ByteBuffer;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;

@MappedJdbcTypes(JdbcType.BINARY)
public class UUIDTypeHandler extends BaseTypeHandler<UUID> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, UUID parameter, JdbcType jdbcType)
            throws SQLException {
        ps.setBytes(i, uuidToBytes(parameter));
    }

    @Override
    public UUID getNullableResult(ResultSet rs, String columnName) throws SQLException {
        byte[] bytes = rs.getBytes(columnName);
        return bytes != null ?
                bytesToUUID(bytes) :
                null;
    }

    @Override
    public UUID getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        byte[] bytes = rs.getBytes(columnIndex);
        return bytes != null ?
                bytesToUUID(bytes) :
                null;
    }

    @Override
    public UUID getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        byte[] bytes = cs.getBytes(columnIndex);
        return bytes != null ?
                bytesToUUID(bytes) :
                null;
    }

    /**
     * {@code UUID} 를 길이 16 인 {@code byte[]} 로 바꿔주는 메서드
     *
     * @param uuid {@code UUID}
     * @return {@code byte[16]}
     */
    private byte[] uuidToBytes(UUID uuid) {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return bb.array();
    }

    /**
     * {@code byte[]} 를 {@code UUID} 로 바꿔주는 메서드
     *
     * @param bytes {@code byte[]}
     * @return {@code UUID}
     */
    private UUID bytesToUUID(byte[] bytes) {
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        long mostSignificantBits = bb.getLong();
        long leastSignificantBits = bb.getLong();
        return new UUID(mostSignificantBits, leastSignificantBits);
    }
}
