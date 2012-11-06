package cn.qylk.LastFm;

import java.io.IOException;
import java.net.URLEncoder;

import cn.qylk.utils.WebUtils;

/**
 * @author qylk2012<div> all rights resolved
 * 
 */
public class Lastfm {
	private static final String apikey = "&api_key=35cf6e34e70af77b53461f82bec1d74c";
	private static final String bio = "http://ws.audioscrobbler.com/2.0/?format=json&method=artist.getinfo&artist=";
	private static final String pic = "http://ws.audioscrobbler.com/2.0/?format=json&method=artist.getimages&artist=";

	// private static final String track ="http://ws.audioscrobbler.com/2.0/?format=json&limit=5&method=track.search&track=";

	/**
	 * 获取歌手信息，Last.FM支持
	 * 
	 * @param artist
	 * @return json结果
	 */
	public String ArtistInfoSearch(String artist) throws IOException {
		artist = URLEncoder.encode(artist, "UTF-8");
		return WebUtils.GetContent(BuildBIOUrl(artist), "UTF-8");
	}

	private String BuildBIOUrl(String artist) {
		return bio + artist + apikey + "&lang=zh";
	}

	private String BuildPICUrl(String artist) {
		return pic + artist + apikey;
	}

	/**
	 * 搜索图片
	 * 
	 * @param artist
	 * @return json数据
	 * @throws IOException
	 */
	public String PICSearch(String artist) throws IOException { // 访问api
		String art = URLEncoder.encode(artist, "UTF-8");
		return WebUtils.GetContent(BuildPICUrl(art), "UTF-8");
	}
}
