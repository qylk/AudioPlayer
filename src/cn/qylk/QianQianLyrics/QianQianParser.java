package cn.qylk.QianQianLyrics;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.res.XmlResourceParser;

public class QianQianParser {
	public static List<LyricResults> parseXml(String xml) {
		List<LyricResults> list = new ArrayList<LyricResults>();
		try {
			XmlPullParser xrp = XmlPullParserFactory.newInstance()
					.newPullParser();
			xrp.setInput(new StringReader(xml));
			// 直到文档的结尾处
			while (xrp.getEventType() != XmlResourceParser.END_DOCUMENT) {
				// 如果遇到了开始标签
				if (xrp.getEventType() == XmlResourceParser.START_TAG) {
					String tagName = xrp.getName();// 获取标签的名字
					if (tagName.equals("lrc")) {
						LyricResults result = new LyricResults();
						result.id = Integer.valueOf(xrp.getAttributeValue(0));// 通过属性名来获取属性值
						result.artist = xrp.getAttributeValue(1);// 通过属性索引来获取属性值
						result.track = xrp.getAttributeValue(2);
						list.add(result);
					}
				}
				xrp.next();// 获取解析下一个事件
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list.size() == 0 ? null : list;
	}
}
