package schedule;

public record Time(int hourValue, TImeType TImeType) {
    public Time copy() {
        return new Time(hourValue, TImeType);
    }

    public int get24HourValue() {
        if(TImeType.equals(TImeType.PM) && this.hourValue < 12) {
            return hourValue + 12;
        }
        if(TImeType.equals(TImeType.AM) && this.hourValue == 12) {
            return 0;
        }
        return hourValue;
    }

    public int differenceBetween(Time other) {
        int convertedHourValue1 = this.get24HourValue();
        int convertedHourValue2 = other.get24HourValue();

        return Math.abs(convertedHourValue1 - convertedHourValue2);
    }
    public String niceFormat() {
        return hourValue + TImeType.name();
    }
}
