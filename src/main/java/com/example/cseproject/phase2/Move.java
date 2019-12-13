package com.example.cseproject.phase2;


import com.example.cseproject.interfaces.DistrictInterface;
import com.example.cseproject.interfaces.PrecinctInterface;

public class Move<Precinct extends PrecinctInterface, District extends DistrictInterface<Precinct>> {
		private District to;
        private District from;
        private Precinct precinct;

        public Move(District to, District from, Precinct precinct) {
        	this.to = to;
            this.from = from;
            this.precinct = precinct;
        }

        public void execute() {
        	from.removePrecinct(precinct);
            to.addPrecinct(precinct);
        }

        public void undo() {
            to.removePrecinct(precinct);
            from.addPrecinct(precinct);
        }


        public Boolean equal(Move move) {
            if (move == null) {
                return false;
            }
            return move.getTo().getId() == this.to.getId() &&
                    move.getFrom().getId() == this.from.getId();
        }

        public String toString() {
            String toID = to!=null?to.getId().toString():"NULL";
            String fromID = from!=null?from.getId().toString():"NULL";
            String precinctID = precinct!=null?precinct.getId().toString():"NULL";
            return "{ "+"\"to\": \""+toID+"\", \"from\": \""+fromID+"\", \"precinct\": \""+precinctID+"\" }";
        }

        public District getTo() {
            return to;
        }

        public District getFrom() {
            return from;
        }

        public Precinct getPrecinct() {
            return precinct;
        }
}