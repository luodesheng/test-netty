package lds.gw.model;

public class Field8583 extends Model {

	private static final long serialVersionUID = 1089041971745363629L;

	private String id;
	private String name;
	private String type;
	private String sourceType;
	private int sourceLength;
	private int length;
	private String align;
	private String sourceLengthType;
	private String convert;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	public int getSourceLength() {
		return sourceLength;
	}

	public void setSourceLength(int sourceLength) {
		this.sourceLength = sourceLength;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public String getAlign() {
		return align;
	}

	public void setAlign(String align) {
		this.align = align;
	}

	public String getSourceLengthType() {
		return sourceLengthType;
	}

	public void setSourceLengthType(String sourceLengthType) {
		this.sourceLengthType = sourceLengthType;
	}

	public String getConvert() {
		return convert;
	}

	public void setConvert(String convert) {
		this.convert = convert;
	}
}
