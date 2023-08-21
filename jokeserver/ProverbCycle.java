class JokeCycle {

    String _JA; // represents 1st joke in joke cycle
    String _JB; // represents 2nd joke in joke cycle 
    String _JC; // represents 3rd joke in joke cycle
    String _JD; // represents 4th joke in joke cycle

    @Override
    public String toString() {
        String line = "-------------------------------------------------------------------";
        return String.format("%s\nJA:%s\nJB:%s\nJC:%s\nJD:%s\n%s", line, this._JA, this._JB, this._JC, this._JD, line);
    }
}

class ProverbCycle {

    String _PA; // represents 1st proverb in proverb cycle
    String _PB;// represents 2nd proverb in proverb cycle
    String _PC;// represents 3rd proverb in proverb cycle
    String _PD;// represents 4th proverb in proverb cycle

    @Override
    public String toString() {
        String line = "-------------------------------------------------------------------";
        return String.format("%s\nJA:%s\nJB:%s\nJC:%s\nJD:%s\n%s", line, this._PA, this._PB, this._PC, this._PD, line);
    }
}


class SwitchState {

    int state = 0; // 

    public int putState() {    //switches state  from joke to proverb and proverb to joke
       state = (state ==0) ? 1:0;
       return state;
    }

    public int currentState() {    // read the present server mode
        return state;
    }
}