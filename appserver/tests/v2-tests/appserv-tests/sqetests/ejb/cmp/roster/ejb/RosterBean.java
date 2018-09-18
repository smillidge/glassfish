/*
 * Copyright (c) 2001, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package com.sun.s1peqe.ejb.cmp.roster.ejb;

import java.util.*;
import javax.ejb.*;
import javax.ejb.*;
import javax.naming.*;
import com.sun.s1peqe.ejb.cmp.roster.util.*;

public class RosterBean implements SessionBean {

    private  LocalPlayerHome playerHome = null;
    private  LocalTeamHome teamHome = null;
    private  LocalLeagueHome leagueHome = null;

    // Player business methods
    public ArrayList testFinder(String parm1, String parm2,
        String parm3) {

        Debug.print("RosterBean testFinder");
        Collection players = null;

        try {
            players = playerHome.findByTest(parm1, parm2, parm3);
        } catch (Exception ex) {
            throw new EJBException(ex.getMessage());
        }
        return copyPlayersToDetails(players);
    }

    public void createPlayer(PlayerDetails details) { 
        Debug.print("RosterBean createPlayer");
        try {
            LocalPlayer player = playerHome.create(details.getId(), 
                details.getName(), details.getPosition(), details.getSalary());
        } catch (Exception ex) {
            throw new EJBException(ex.getMessage());
        }
    }
 
    public void addPlayer(String playerId, String teamId) { 
        Debug.print("RosterBean addPlayer");
        try {
            LocalTeam team = teamHome.findByPrimaryKey(teamId);
            LocalPlayer player = playerHome.findByPrimaryKey(playerId);
            team.addPlayer(player);
        } catch (Exception ex) {
            throw new EJBException(ex.getMessage());
        }
    }

    public void removePlayer(String playerId) { 
        Debug.print("RosterBean removePlayer");
        try {
            LocalPlayer player = playerHome.findByPrimaryKey(playerId);
            player.remove();
        } catch (Exception ex) {
            throw new EJBException(ex.getMessage());
        }
    }
 
    public void dropPlayer(String playerId, String teamId) {
        Debug.print("RosterBean dropPlayer");
        try {
            LocalPlayer player = playerHome.findByPrimaryKey(playerId);
            LocalTeam team = teamHome.findByPrimaryKey(teamId);
            team.dropPlayer(player);
        } catch (Exception ex) {
            throw new EJBException(ex.getMessage());
        }
    }

    public PlayerDetails getPlayer(String playerId) {
        Debug.print("RosterBean getPlayer");
        PlayerDetails playerDetails = null;
        try {
            LocalPlayer player = playerHome.findByPrimaryKey(playerId);
            playerDetails = new PlayerDetails(playerId,
                player.getName(), player.getPosition(), player.getSalary());
        } catch (Exception ex) {
            throw new EJBException(ex.getMessage());
        }
        return playerDetails;
    }

    public ArrayList getPlayersOfTeam(String teamId) { 
        Debug.print("RosterBean getPlayersOfTeam");
        Collection players = null;

        try {
            LocalTeam team = teamHome.findByPrimaryKey(teamId);
            players = team.getPlayers();
        } catch (Exception ex) {
            throw new EJBException(ex.getMessage());
        }

        return copyPlayersToDetails(players);
    }


    public ArrayList getPlayersOfTeamCopy(String teamId) { 
        Debug.print("RosterBean getPlayersOfTeamCopy");
        ArrayList playersList = null;

        try {
            LocalTeam team = teamHome.findByPrimaryKey(teamId);
            playersList = team.getCopyOfPlayers();
        } catch (Exception ex) {
            throw new EJBException(ex.getMessage());
        }

        return playersList;
    }


    public ArrayList getTeamsOfLeague(String leagueId) { 
        Debug.print("RosterBean getTeamsOfLeague");

        ArrayList detailsList = new ArrayList();
        Collection teams = null;
        try {
            LocalLeague league = leagueHome.findByPrimaryKey(leagueId);
            teams = league.getTeams();
        } catch (Exception ex) {
            throw new EJBException(ex.getMessage());
        }

        Iterator i = teams.iterator();
        while (i.hasNext()) {
            LocalTeam team = (LocalTeam) i.next();
            TeamDetails details = new TeamDetails(team.getTeamId(),
                team.getName(), team.getCity());
            detailsList.add(details);
        }
        return detailsList;
    }


    public ArrayList getPlayersByPosition(String position) {
        Debug.print("RosterBean getPlayersByPosition");
        Collection players = null;

        try {
            players = playerHome.findByPosition(position);
        } catch (Exception ex) {
            throw new EJBException(ex.getMessage());
        }
        return copyPlayersToDetails(players);
    }


    public ArrayList getPlayersByHigherSalary(String name) { 
        Debug.print("RosterBean getPlayersByByHigherSalary");
        Collection players = null;

        try {
            players = playerHome.findByHigherSalary(name);
        } catch (Exception ex) {
            throw new EJBException(ex.getMessage());
        }
        return copyPlayersToDetails(players);
    } // getPlayersByHigherSalary

    public ArrayList getPlayersBySalaryRange(double low, double high) { 
        Debug.print("RosterBean getPlayersBySalaryRange");
        Collection players = null;

        try {
            players = playerHome.findBySalaryRange(low, high);
        } catch (Exception ex) {
            throw new EJBException(ex.getMessage());
        }

        return copyPlayersToDetails(players);
    } // getPlayersBySalaryRange

    public ArrayList getPlayersByLeagueId(String leagueId) { 
        Debug.print("RosterBean getPlayersByLeagueId");
        Collection players = null;

        try {
            LocalLeague league = leagueHome.findByPrimaryKey(leagueId);
            players = playerHome.findByLeague(league);
        } catch (Exception ex) {
            throw new EJBException(ex.getMessage());
        }
        return copyPlayersToDetails(players);
    } // getPlayersByLeagueId

    public ArrayList getPlayersBySport(String sport) { 
        Debug.print("RosterBean getPlayersBySport");
        Collection players = null;

        try {
            players = playerHome.findBySport(sport);
        } catch (Exception ex) {
            throw new EJBException(ex.getMessage());
        }
        return copyPlayersToDetails(players);
    } // getPlayersBySport

    public ArrayList getPlayersByCity(String city) { 
        Debug.print("RosterBean getPlayersByCity");
        Collection players = null;

        try {
            players = playerHome.findByCity(city);
        } catch (Exception ex) {
            throw new EJBException(ex.getMessage());
        }
        return copyPlayersToDetails(players);
    } // getPlayersByCity

    public ArrayList getAllPlayers() { 
        Debug.print("RosterBean getAllPlayers");
        Collection players = null;

        try {
            players = playerHome.findAll();
        } catch (Exception ex) {
            throw new EJBException(ex.getMessage());
        }

        return copyPlayersToDetails(players);
    } // getAllPlayers

    public ArrayList getPlayersNotOnTeam() { 
        Debug.print("RosterBean getPlayersNotOnTeam");
        Collection players = null;

        try {
            players = playerHome.findNotOnTeam();
        } catch (Exception ex) {
            throw new EJBException(ex.getMessage());
        }
        return copyPlayersToDetails(players);
    } // getPlayersNotOnTeam

    public ArrayList getPlayersByPositionAndName(String position, 
        String name) { 
        Debug.print("RosterBean getPlayersByPositionAndName");
        Collection players = null;

        try {
            players = playerHome.findByPositionAndName(position, name);
        } catch (Exception ex) {
            throw new EJBException(ex.getMessage());
        }
        return copyPlayersToDetails(players);
    } // getPlayersByPositionAndName

    public ArrayList getLeaguesOfPlayer(String playerId) { 
        Debug.print("RosterBean getLeaguesOfPlayer");
        ArrayList detailsList = new ArrayList();
        Collection leagues = null;

        try {
            LocalPlayer player = playerHome.findByPrimaryKey(playerId);
            leagues = player.getLeagues();
        } catch (Exception ex) {
            throw new EJBException(ex.getMessage());
        }
      
        Iterator i = leagues.iterator();
        while (i.hasNext()) {
            LocalLeague league = (LocalLeague) i.next();
            LeagueDetails details = new LeagueDetails(league.getLeagueId(),
                                                      league.getName(),
                                                      league.getSport());
            detailsList.add(details);
        }
        return detailsList;
    } // getLeaguesOfPlayer

    public ArrayList getSportsOfPlayer(String playerId) { 
        Debug.print("RosterBean getSportsOfPlayer");
        ArrayList sportsList = new ArrayList();
        Collection sports = null;

        try {
            LocalPlayer player = playerHome.findByPrimaryKey(playerId);
            sports = player.getSports();
        } catch (Exception ex) {
            throw new EJBException(ex.getMessage());
        }
      
        Iterator i = sports.iterator();
        while (i.hasNext()) {
            String sport = (String) i.next();
            sportsList.add(sport);
        }
        return sportsList;
    } // getSportsOfPlayer

    // Team business methods
    public void createTeamInLeague(TeamDetails details, String leagueId) { 
        Debug.print("RosterBean createTeamInLeague");
        try {
            LocalLeague league = leagueHome.findByPrimaryKey(leagueId);
            LocalTeam team = teamHome.create(details.getId(),
                                             details.getName(),
                                             details.getCity());
            league.addTeam(team);
        } catch (Exception ex) {
            throw new EJBException(ex.getMessage());
        }
    }
 
    public void removeTeam(String teamId) { 
        Debug.print("RosterBean removeTeam");
        try {
            LocalTeam team = teamHome.findByPrimaryKey(teamId);
            team.remove();
        } catch (Exception ex) {
            throw new EJBException(ex.getMessage());
        }
    }
 
    public TeamDetails getTeam(String teamId) {
        Debug.print("RosterBean getTeam");
        TeamDetails teamDetails = null;
        try {
            LocalTeam team = teamHome.findByPrimaryKey(teamId);
            teamDetails = new TeamDetails(teamId,
                                          team.getName(), team.getCity());
        } catch (Exception ex) {
            throw new EJBException(ex.getMessage());
        }
        return teamDetails;
    }

    // League business methods
    public void createLeague(LeagueDetails details) { 
        Debug.print("RosterBean createLeague");
        try {
            LocalLeague league = leagueHome.create(details.getId(), 
                                                   details.getName(),
                                                   details.getSport());
        } catch (Exception ex) {
            throw new EJBException(ex.getMessage());
        }
    }
 
    public void removeLeague(String leagueId) { 
        Debug.print("RosterBean removeLeague");
        try {
            LocalLeague league = leagueHome.findByPrimaryKey(leagueId);
            league.remove();
        } catch (Exception ex) {
            throw new EJBException(ex.getMessage());
        }
    }
 
    public LeagueDetails getLeague(String leagueId) {
        Debug.print("RosterBean getLeague");
        LeagueDetails leagueDetails = null;
        try {
            LocalLeague league = leagueHome.findByPrimaryKey(leagueId);
            leagueDetails = new LeagueDetails(leagueId,
                                              league.getName(),
                                              league.getSport());
        } catch (Exception ex) {
            throw new EJBException(ex.getMessage());
        }
        return leagueDetails;
    }
 
    // SessionBean methods
    public void ejbCreate() throws CreateException {
        Debug.print("RosterBean ejbCreate");
        try {
            playerHome = lookupPlayer();
            teamHome = lookupTeam();
            leagueHome = lookupLeague();
        } catch (NamingException ex) {
            throw new CreateException(ex.getMessage());
        }
    }

    public void ejbActivate() {
        Debug.print("RosterBean ejbActivate");
        try {
            playerHome = lookupPlayer();
            teamHome = lookupTeam();
            leagueHome = lookupLeague();
        } catch (NamingException ex) {
            throw new EJBException(ex.getMessage());
        }
    }

    public void ejbPassivate() {
        playerHome = null;
        teamHome = null;
        leagueHome = null;
    }

    public RosterBean() {}
    public void ejbRemove() {}
    public void setSessionContext(SessionContext sc) {}
 
    // Private methods
    private LocalPlayerHome lookupPlayer() throws NamingException {
        Context initial = new InitialContext();
        Object objref = initial.lookup("java:comp/env/ejb/SimplePlayer");
        return (LocalPlayerHome) objref;
    }

   private LocalTeamHome lookupTeam() throws NamingException {
       Context initial = new InitialContext();
       Object objref = initial.lookup("java:comp/env/ejb/SimpleTeam");
       return (LocalTeamHome) objref;
   }

   private LocalLeagueHome lookupLeague() throws NamingException {
       Context initial = new InitialContext();
       Object objref = initial.lookup("java:comp/env/ejb/SimpleLeague");
       return (LocalLeagueHome) objref;
   }

   private ArrayList copyPlayersToDetails(Collection players) {
       ArrayList detailsList = new ArrayList();
       Iterator i = players.iterator();

       while (i.hasNext()) {
           LocalPlayer player = (LocalPlayer) i.next();
           PlayerDetails details = new PlayerDetails(player.getPlayerId(),
                                                     player.getName(),
                                                     player.getPosition(),
                                                     player.getSalary());
           detailsList.add(details);
       }
       return detailsList;
   }
}
