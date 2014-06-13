package com.qylk.app.musicplayer.utils;

public class TrackTAG {
	public int lang, genre, rhythm, sing_method, age, subject, instrument;
	public int lib_id;

	public TrackTAG(int lang, int genre, int rhythm, int sing_method, int age,
			int subject, int instrument, int lib_id) {
		this.lang = lang;
		this.genre = genre;
		this.rhythm = rhythm;
		this.sing_method = sing_method;
		this.age = age;
		this.subject = subject;
		this.instrument = instrument;
		this.lib_id = lib_id;
	}

	public int[] getTagIntArrray() {
		int[] tag = new int[7];
		tag[0] = this.lang;
		tag[1] = this.genre;
		tag[2] = this.rhythm;
		tag[3] = this.sing_method;
		tag[4] = this.age;
		tag[5] = this.subject;
		tag[6] = this.instrument;
		return tag;
	}

	public int getLib_Id() {
		return lib_id;
	}

	@Override
	public String toString() {
		return new StringBuffer().append("lang").append(':').append(lang)
				.append("\n").append("genre").append(':').append(genre)
				.append("\n").append("rhythm").append(':').append(rhythm)
				.append("\n").append("sing_method").append(':')
				.append(sing_method).append("\n").append("age").append(':')
				.append(age).append("\n").append("subject").append(':')
				.append(subject).append("\n").append("instrument").append(':')
				.append(instrument).append("\n").append("lib_id").append(':')
				.append(lib_id).toString();
	}
}
