package model.account;

import java.util.Map;

public class Account {
	
	public String name;
	public double balance;
	public long create_time;
	public GasInfo gas_info;
	public RamInfo ram_info;
	public Map<String, Permission> permissions;
	public Map<String, Group> groups;
	public FrozenBalance[] frozen_balances;
	public VoteInfo[] vote_infos;
}
