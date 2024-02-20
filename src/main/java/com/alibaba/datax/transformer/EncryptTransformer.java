package com.alibaba.datax.transformer;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SmUtil;
import com.alibaba.datax.common.element.Record;
import com.alibaba.datax.common.element.StringColumn;
import com.alibaba.datax.common.exception.DataXException;
import com.alibaba.datax.common.spi.Writer;
import com.alibaba.datax.core.transport.transformer.TransformerErrorCode;
import com.alibaba.datax.transformer.support.AlgorithmType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class EncryptTransformer extends Transformer {

    private static final Logger LOG = LoggerFactory.getLogger(Writer.Job.class);

    public EncryptTransformer(){
        super.setTransformerName("encrypttransformer");
    }

    @Override
    public Record evaluate(Record record, Object... paras) {
        int columnIndex;
        String encryptType;
        String encryptKey;

        if (paras.length != 3) {
            throw DataXException.asDataXException(TransformerErrorCode.TRANSFORMER_ILLEGAL_PARAMETER, "paras:" + Arrays.asList(paras));
        }

        columnIndex = Integer.parseInt(paras[0].toString());
        encryptType = (String) paras[1];
        encryptKey = (String) paras[2];

        AlgorithmType algorithmType = EnumUtil.getBy(AlgorithmType.class, (e) -> e.getCode().equals(encryptType));

        if (algorithmType == null) {
            throw DataXException.asDataXException(TransformerErrorCode.TRANSFORMER_ILLEGAL_PARAMETER, "加密类型错误->paras:" + Arrays.asList(paras));
        }

        try {
            // 加密
            encryptColumn(record, columnIndex, encryptKey, algorithmType);
        } catch (Exception e) {
            throw DataXException.asDataXException(TransformerErrorCode.TRANSFORMER_RUN_EXCEPTION, e.getMessage(), e);
        }

        return record;
    }

    private void encryptColumn(Record record, Integer columnIndex, String encryptKey, AlgorithmType algorithmType) {
        try {
            String oriValue = record.getColumn(columnIndex).asString();

            if (StrUtil.isBlank(oriValue)) {
                return;
            }

            String encryptValue = "error";

            if (algorithmType == AlgorithmType.SM4_ECB) {
                encryptValue = SmUtil.sm4(encryptKey.getBytes(CharsetUtil.CHARSET_UTF_8)).encryptHex(oriValue);
            }

            record.setColumn(columnIndex, new StringColumn(encryptValue));
        } catch (Exception e) {
            throw DataXException.asDataXException(TransformerErrorCode.TRANSFORMER_RUN_EXCEPTION, e.getMessage(), e);
        }

    }

}
