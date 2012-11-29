package utils;


import agents.ArmyUnit;

public class NegotiationOffer implements Comparable<NegotiationOffer> {

	private ArmyUnit owner;
	private DirectionList preferences;

	public NegotiationOffer(ArmyUnit owner, DirectionList preferences) {

		this.setOwner(owner);
		this.setPreferences(preferences);
	}

	@Override
	public int compareTo(NegotiationOffer o) {

		if (preferences.compareTo(o.getPreferences()) == 0) {
			switch (owner.getValue()) {
				case Captain:
					return -1;
				case Soldier:
					switch (o.getOwner().getValue()) {
					case Captain:
						return 1;
					case Soldier:
						return 0;
					case Robot:
						return -1;
					default:
						break;
					}
				case Robot:
					return 1;
	
				default:
					return 0;
	
				}

		}
		
		return preferences.compareTo(o.getPreferences());

	}

	/**
	 * @return the owner
	 */
	public ArmyUnit getOwner() {
		return owner;
	}

	/**
	 * @param owner
	 *            the owner to set
	 */
	public void setOwner(ArmyUnit owner) {
		this.owner = owner;
	}

	/**
	 * @return the preferences
	 */
	public DirectionList getPreferences() {
		return preferences;
	}

	/**
	 * @param preferences
	 *            the preferences to set
	 */
	public void setPreferences(DirectionList preferences) {
		this.preferences = preferences;
	}
	
	@Override
	public String toString(){
		
		return owner.toString() + " : " + preferences.toString();
	}

}
