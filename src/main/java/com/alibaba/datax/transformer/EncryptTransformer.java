package com.alibaba.datax.transformer;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SmUtil;
import com.alibaba.datax.common.element.Record;
import com.alibaba.datax.common.element.StringColumn;
import com.alibaba.datax.common.exception.DataXException;
import com.alibaba.datax.core.transport.transformer.TransformerErrorCode;
import com.alibaba.datax.transformer.support.AlgorithmType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class EncryptTransformer extends ComplexTransformer {

    private static final Logger LOG = LoggerFactory.getLogger(EncryptTransformer.class);

    @Override
    public Record evaluate(Record record, Map<String, Object> tContext, Object... paras) {
        LOG.debug("Paras: " + Arrays.toString(paras));
        LOG.debug("tContext: " + (tContext != null ? tContext.toString() : ""));

        if (tContext == null) {
            throw DataXException.asDataXException(TransformerErrorCode.TRANSFORMER_ILLEGAL_PARAMETER, "context: null");
        }

        if (paras.length != 1) {
            throw DataXException.asDataXException(TransformerErrorCode.TRANSFORMER_ILLEGAL_PARAMETER, "paras:" + Arrays.asList(paras));
        }

        String encryptType = (String) tContext.getOrDefault("encryptType", AlgorithmType.SM4_ECB.getCode());
        String encryptKey = (String) tContext.get("encryptKey");
        @SuppressWarnings("unchecked") List<Integer> columns = (List<Integer>) tContext.get("columns");


        AlgorithmType algorithmType = EnumUtil.getBy(AlgorithmType.class, (e) -> e.getCode().equals(encryptType));

        if (algorithmType == null) {
            throw DataXException.asDataXException(TransformerErrorCode.TRANSFORMER_ILLEGAL_PARAMETER, "加密类型错误->paras:" + Arrays.asList(paras));
        }

        try {
            for (Integer colIdx : columns) {
                encryptColumn(record, colIdx, encryptKey, algorithmType);
            }
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
