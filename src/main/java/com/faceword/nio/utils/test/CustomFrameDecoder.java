package com.faceword.nio.utils.test;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

/**
 * @Author: zyong
 * @Date: 2018/11/1 15:42
 * @Version 1.0
 */
public class CustomFrameDecoder  extends ByteToMessageDecoder {
    private final Logger logger= LoggerFactory.getLogger(CustomFrameDecoder.class);
    private static int HEADER_SIZE = 16;
    private final ByteBuf delimiter;
    private final int maxFrameLength;
    private static ByteBuf buf = Unpooled.buffer();

    public CustomFrameDecoder(int maxFrameLength,ByteBuf delimiter ) {
        this.delimiter = delimiter;
        this.maxFrameLength = maxFrameLength;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        Object decoded = this.decode(ctx, in);
        if(decoded != null) {
            out.add(decoded);
        }
    }

    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        in.markReaderIndex();
        int headerOffset = indexOf(in, delimiter);
        if (headerOffset < 0) {
            in.resetReaderIndex();
            return null;
        } else {
            in.skipBytes(headerOffset + 5);
        }
        int bodyLength=bodyLenght(headerOffset,in);
        if(in.readableBytes()<bodyLength){
            in.resetReaderIndex();
            return null;
        }else {
            in.readBytes(buf, bodyLength);
            ByteBuf frame = ctx.alloc().buffer(bodyLength);
            frame.writeBytes(buf);
            buf.clear();
            return frame;
        }
    }

    private static int indexOf(ByteBuf haystack, ByteBuf needle) {
        for(int i = haystack.readerIndex(); i < haystack.writerIndex(); ++i) {
            int haystackIndex = i;
            int needleIndex;
            for(needleIndex = 0; needleIndex < needle.capacity() && haystack.getByte(haystackIndex) == needle.getByte(needleIndex); ++needleIndex) {
                ++haystackIndex;
                if(haystackIndex == haystack.writerIndex() && needleIndex != needle.capacity() - 1) {
                    return -1;
                }
            }
            if(needleIndex == needle.capacity()) {
                return i - haystack.readerIndex();
            }
        }

        return -1;
    }

    /**
     * 功能：获取信息头中的长度
     * */
    private  int bodyLenght(int header,ByteBuf buf){
        int bodyLength=0;
        int headerIndex=header+1,headerEnd=header+4;
        for(;headerIndex<=headerEnd;headerIndex++){
            bodyLength*=10;
            bodyLength+=(int)buf.getByte(headerIndex)-48;
        }
        return bodyLength;
    }
}

