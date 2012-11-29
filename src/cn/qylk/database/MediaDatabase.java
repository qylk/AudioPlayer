package cn.qylk.database;

import java.io.File;
import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import cn.qylk.app.APP;
import cn.qylk.app.ListTypeInfo;
import cn.qylk.app.TrackInfo;

/**
 * @author qylk2012 all rights resolved
 * 
 */
public class MediaDatabase {
	private static ContentResolver resolver = APP.getInstance()
			.getContentResolver();

	/**
	 * 获取所有album列表
	 * 
	 * @return
	 */
	public static Cursor AlbumCursor() {
		return resolver.query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
				new String[] { MediaStore.Audio.Albums.ALBUM,
						MediaStore.Audio.Albums.NUMBER_OF_SONGS,
						MediaStore.Audio.Albums.ARTIST ,MediaStore.Audio.Artists._ID}, null, null, null);
	}

	/**
	 * 获取所有artist列表
	 * 
	 * @return
	 */
	public static Cursor ArtistCursor() {
		return resolver.query(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
				new String[] { MediaStore.Audio.Artists.ARTIST,
						MediaStore.Audio.Artists.NUMBER_OF_TRACKS,
						MediaStore.Audio.Artists.ARTIST,MediaStore.Audio.Artists._ID }, null, null, null);
	}

	public static Cursor GetCursor(ListTypeInfo type) {
		String selection = null;
		String sortorder = null;
		String[] args = null;
		switch (type.list) {
		case ALLSONGS:
			sortorder = "title ASC";
			break;
		case ARTIST:
			selection = "artist_id=?";
			args = new String[] { String.valueOf(type.para) };
			sortorder = "artist ASC";
			break;
		case ALBUM:
			selection = "album_id=?";
			args = new String[] { String.valueOf(type.para) };
			sortorder = "album ASC";
			break;
		case PERSONAL:
			selection = MediaStore.Audio.Media._ID + " in ("
					+ PersonalListIDS(type.para) + ")";
			sortorder = "title ASC";
			break;
		case SEARCH:
			selection = "title LIKE '%" + type.keyword + "%' OR artist LIKE '%"
					+ type.keyword + "%'";
			break;
		case HISTORY:
			sortorder = "date_modified DESC LIMIT 10";// 取历史记录10条
			break;
		case LOVE:
			selection = "bookmark=1";
			sortorder = "title ASC";
			break;
		case RECENTADD:
			selection = "date_added >"
					+ (getLastestUpdateTime() - 12 * 60 * 60);// 半天之内添加的
			sortorder = "date_added DESC";
			break;
		default:
			break;
		}
		return resolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				new String[] { MediaStore.Audio.Media._ID,
						MediaStore.Audio.Media.DURATION,
						MediaStore.Audio.Media.ARTIST,
						MediaStore.Audio.Media.DATA,
						MediaStore.Audio.Media.TITLE,
						MediaStore.Audio.Media.ALBUM }, selection, args,
				sortorder);
	}

	// /**
	// * @deprecated use {@link MediaDatabase#GetCursor } instead 实现模糊查询，返回_id
	// * @param 歌曲标题或艺术家
	// * @return 查无结果 返回null；
	// */
	// public static Cursor searchMusic(String what) {
	// if (what.equals(""))
	// return null;
	// Cursor cursor = resolver.query(
	// MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[] {
	// MediaStore.Audio.Media._ID,
	// MediaStore.Audio.Media.DURATION,
	// MediaStore.Audio.Media.ARTIST,
	// MediaStore.Audio.Media.DATA,
	// MediaStore.Audio.Media.TITLE,
	// MediaStore.Audio.Media.ALBUM }, "title like '%" + what
	// + "%' or artist like '%" + what + "%'", null, null);
	// if (cursor.getCount() == 0)
	// return null;
	// return cursor;
	// }

	/**
	 * 获取歌曲id列表
	 * 
	 * @param type
	 * @return
	 */
	public static ArrayList<Integer> getIDS(ListTypeInfo type) {
		Cursor c = GetCursor(type);
		ArrayList<Integer> ids = new ArrayList<Integer>(c.getCount());
		while (c.moveToNext())
			ids.add(c.getInt(0));
		c.close();
		return ids;
	}

	/**
	 * 取得最新添加时间
	 * 
	 * @return
	 */
	private static long getLastestUpdateTime() {
		Cursor cursor = resolver.query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				new String[] { MediaStore.Audio.Media.DATE_ADDED }, null, null,
				"date_added DESC LIMIT 1");
		cursor.moveToFirst();
		long time = cursor.getLong(0);
		cursor.close();
		return time;
	}

	/**
	 * 获取一组指定id的歌曲路径
	 */
	public static File[] GetPaths(Integer[] ids) {
		StringBuilder sb = new StringBuilder();
		for (Integer id : ids)
			sb.append(id).append(',');
		sb.deleteCharAt(sb.length() - 1);
		String idds = sb.toString();
		Cursor cursor = resolver.query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				new String[] { MediaStore.Audio.Media.DATA },
				MediaStore.Audio.Media._ID + " in (" + idds + ")", null, null);
		File[] files = new File[ids.length];
		int i = 0;
		while (cursor.moveToNext())
			files[i++] = new File(cursor.getString(0));
		cursor.close();
		return files;
	}

	/**
	 * 获取所有私有列表名称
	 * 
	 * @return
	 */
	public static Cursor GetPersonalListUsingCursor() {
		return resolver.query(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
				new String[] { MediaStore.Audio.Playlists._ID,
						MediaStore.Audio.Playlists.NAME }, null, null, null);
	}

	/**
	 * 获取所有私有列表名称
	 */
	public static CharSequence[] GetPersonalListUsingList() {
		Cursor c = GetPersonalListUsingCursor();
		int sum = c.getCount();
		CharSequence[] list = new CharSequence[sum + 1];
		int i = 0;
		while (c.moveToNext()) {
			list[i++] = c.getString(1);
		}
		c.close();
		return list;
	}

	/**
	 * 获取指定id的歌曲信息
	 * 
	 * @param id
	 * @return
	 * @param info
	 *            用于查询下一首歌的标题时，参数info应设为null
	 */
	public static TrackInfo getTrackInfo(int id, TrackInfo info) {
		Cursor c = resolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				new String[] { MediaStore.Audio.Media._ID,
						MediaStore.Audio.Media.DURATION,
						MediaStore.Audio.Media.ARTIST,
						MediaStore.Audio.Media.DATA,
						MediaStore.Audio.Media.TITLE,
						MediaStore.Audio.Media.ALBUM,
						MediaStore.Audio.Media.MIME_TYPE,
						MediaStore.Audio.Media.SIZE,
						MediaStore.Audio.Media.YEAR }, "_id=?",
				new String[] { String.valueOf(id) }, null);
		c.moveToFirst();
		if (info == null)// 用于查询下一首歌的标题时，参数info应设为null
			info = new TrackInfo();
		info.id = c.getInt(0);
		info.duration = c.getInt(1);
		info.artist = c.getString(2);
		info.path = c.getString(3);
		info.title = c.getString(4);
		info.album = c.getString(5);
		info.mimetype = c.getString(6);
		info.size = c.getInt(7);
		info.year = c.getString(8);
		c.close();
		return info;
	}

	/**
	 * 批量新增个人列表成员
	 * 
	 * @param playlist_id
	 * @param audio_id
	 */
	public static void InsertMoreToPersonalList(int playlistId,
			Integer[] audioId) {
		Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external",
				playlistId);
		Cursor cursor = resolver.query(uri, new String[] { "count(*)" }, null,
				null, null);
		cursor.moveToLast();
		int base = cursor.getInt(0);
		cursor.close();
		for (int i = 0; i < audioId.length; i++) {
			ContentValues contentValues = new ContentValues();
			contentValues.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER,
					Integer.valueOf(base + i + 1));
			contentValues.put(MediaStore.Audio.Playlists.Members.AUDIO_ID,
					audioId[i]);
			resolver.insert(uri, contentValues);
		}
	}

	/**
	 * 新增个人列表成员
	 * 
	 * @param playlist_id
	 * @param audio_id
	 */
	public static void InsertOneToPersonalList(int playlistId, int audioId) {
		Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external",
				playlistId);
		Cursor cursor = resolver.query(uri, new String[] { "count(*)" }, null,
				null, null);
		cursor.moveToLast();
		int base = cursor.getInt(0);
		cursor.close();
		ContentValues contentValues = new ContentValues();
		contentValues.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER,
				Integer.valueOf(base + 1));
		contentValues.put(MediaStore.Audio.Playlists.Members.AUDIO_ID, audioId);
		resolver.insert(uri, contentValues);
	}

	/**
	 * 是否已经收藏
	 * 
	 * @param id
	 * @return
	 */
	public static boolean IsLoved(int id) {
		Cursor cursor = resolver.query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				new String[] { MediaStore.Audio.Media.BOOKMARK },
				MediaStore.Audio.Media._ID + "=?",
				new String[] { Integer.toString(id) }, null);
		cursor.moveToFirst();
		boolean is = cursor.getInt(0) == 1;
		cursor.close();
		return is;
	}

	/**
	 * 指定名称的个人列表是否已存在
	 * 
	 * @param name
	 * @return 返回-1表示不存在；否则返回其列表ID
	 */
	public static int IsPersonalListExist(String name) {
		Cursor cursor = resolver.query(
				MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
				new String[] { MediaStore.Audio.Playlists._ID },
				MediaStore.Audio.Playlists.NAME + "=?", new String[] { name },
				null);
		return cursor.moveToFirst() ? cursor.getInt(0) : -1;
	}

	/**
	 * 新建私有列表
	 * 
	 * @param name
	 * @return 列表ID
	 */
	public static int NewPersonalListIDS(String name) {
		int resultcode = IsPersonalListExist(name);
		if (resultcode == -1) {
			ContentValues contentValues = new ContentValues();
			contentValues.put(MediaStore.Audio.Playlists.NAME, name);
			resolver.insert(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
					contentValues);
		} else
			return resultcode;
		return IsPersonalListExist(name);
	}

	/**
	 * 获取指定id列表内容的id组，，用于进一步查询这些id对应的其他内容
	 * 
	 * @param PersonalList
	 *            _id 结果形如(2,5,9....)的字符串
	 * @return
	 */
	private static String PersonalListIDS(int playlistId) {
		Cursor cursor = resolver.query(MediaStore.Audio.Playlists.Members
				.getContentUri("external", playlistId),
				new String[] { MediaStore.Audio.Playlists.Members.AUDIO_ID },
				null, null, MediaStore.Audio.Playlists.Members.PLAY_ORDER
						+ " DESC");
		StringBuilder sb = new StringBuilder();
		while (cursor.moveToNext()) {
			sb.append(cursor.getInt(0)).append(',');
		}
		if (sb.length() != 0)
			sb.deleteCharAt(sb.length() - 1);
		else
			sb.append('0');
		cursor.close();
		return sb.toString();
	}

	public static void recordLove(int id, boolean islove) {
		ContentValues cv = new ContentValues();
		cv.put(MediaStore.Audio.Media.BOOKMARK, islove ? 1 : 0);
		resolver.update(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, cv,
				MediaStore.Audio.Media._ID + "=?",
				new String[] { Integer.toString(id) });
	}

	/**
	 * 删除数据库专辑
	 * 
	 * @param musicid
	 */
	public static void removeAlbum(String album) {
		resolver.delete(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
				MediaStore.Audio.Albums.ALBUM + "=?", new String[] { album });
	}

	/**
	 * 删除数据库艺术家
	 * 
	 * @param musicid
	 */
	public static void removeArtist(String artist) {
		resolver.delete(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
				MediaStore.Audio.Artists.ARTIST + "=?", new String[] { artist });
	}

	/**
	 * 删除数据库歌曲记录
	 * 
	 * @param musicid
	 */
	public static void removeAudio(int id) {
		resolver.delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, "_id=?",
				new String[] { Integer.toString(id) });
	}

	/**
	 * 移除条目
	 * 
	 * @param name
	 */
	public static void RemoveOneFromPersonalList(int playlistId, int audioId) {
		resolver.delete(MediaStore.Audio.Playlists.Members.getContentUri(
				"external", playlistId),
				MediaStore.Audio.Playlists.Members.AUDIO_ID + "=?",
				new String[] { String.valueOf(audioId) });
	}

	/**
	 * 移除个人列表
	 * 
	 * @param musicid
	 */
	public static void removePersonalList(String name) {
		resolver.delete(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
				"name=?", new String[] { name });
	}

	/**
	 * 移除个人列表
	 * 
	 * @param musicid
	 */
	public static void RemovePersonalList(int playlistId) {
		resolver.delete(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
				"_id=?", new String[] { String.valueOf(playlistId) });
	}

	/**
	 * 重命名个人列表
	 * 
	 * @param name
	 */
	public static void RenamePersonalList(int playlistId, String newname) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(MediaStore.Audio.Playlists.NAME, newname);
		resolver.update(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
				contentValues, MediaStore.Audio.Playlists._ID + "=?",
				new String[] { String.valueOf(playlistId) });
	}

	/**
	 * 设置铃声
	 * 
	 * @param audioid
	 * @param type
	 * <BR>
	 *            0：RINGTONE<br>
	 *            1：ALARM
	 */
	public static void setAlarm(int audioid, int type) {
		ContentValues cv = new ContentValues();
		cv.put(type == 0 ? MediaStore.Audio.Media.IS_RINGTONE
				: MediaStore.Audio.Media.IS_ALARM, true);
		resolver.update(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, cv,
				"_id=?", new String[] { String.valueOf(audioid) });
	}

	/**
	 * 检查数据库是否建立
	 * 
	 * @return
	 */
	public static boolean TestDB() {
		Cursor cursor = resolver.query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				new String[] { MediaStore.Audio.Media._ID }, null, null, null);
		if (cursor != null) {
			boolean ok = cursor.moveToNext();
			cursor.close();
			return ok;
		} else
			return false;
	}

	/**
	 * 记录播放时间
	 * 
	 * @param id
	 */
	public static void TimeRecode(int id) {
		ContentValues cv = new ContentValues();
		cv.put(MediaStore.Audio.Media.DATE_MODIFIED,
				System.currentTimeMillis() / 1000);
		resolver.update(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, cv,
				MediaStore.Audio.Media._ID + "=?",
				new String[] { Integer.toString(id) });
	}

	/**
	 * 歌曲收藏
	 * 
	 * @param id
	 */
	public static void UpdateLoveAudio(int id, Boolean islove) {
		ContentValues cv = new ContentValues();
		cv.put(MediaStore.Audio.Media.BOOKMARK, islove);
		resolver.update(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, cv,
				MediaStore.Audio.Media._ID + "=?",
				new String[] { Integer.toString(id) });
	}

	/**
	 * 更新指定id的条目信息
	 */
	public static void updateTrackInfo(TrackInfo info) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(MediaStore.Audio.Media.TITLE, info.title);
		contentValues.put(MediaStore.Audio.Media.ARTIST, info.artist);
		contentValues.put(MediaStore.Audio.Media.ALBUM, info.album);
		resolver.update(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				contentValues, "_id=?",
				new String[] { Integer.toString(info.id) });
	}

	/**
	 * 视频列表
	 * 
	 * @return
	 */
	public static Cursor VideoCursor() {
		return resolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
				new String[] { MediaStore.Video.Media.TITLE,
						MediaStore.Video.Media.MIME_TYPE,
						MediaStore.Video.Media.DURATION,
						MediaStore.Video.Media.DATA }, null, null, null);
	}
}
