
/***
 * Excerpted from "Learn to Program with Minecraft Plugins, CanaryMod Edition",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/ahmine2 for more book information.
***/
package dna;

import net.canarymod.database.Column;
import net.canarymod.database.Column.DataType;
import net.canarymod.database.DataAccess;

public class StatsDna extends DataAccess {
  @Column(columnName = "player_name",
          columnType = Column.ColumnType.PRIMARY,
          dataType   = DataType.STRING)
  public String player_name;
  
  @Column(columnName = "gespielte_spiele", dataType = DataType.INTEGER)
  public int playedgames;
  
  @Column(columnName = "gesprungene_bloecke", dataType = DataType.INTEGER)
  public int jumpedblocks;
  
  @Column(columnName = "null_fails", dataType = DataType.INTEGER)
  public int perfectwin;

  @Column(columnName = "gesamt_fails", dataType = DataType.INTEGER)
  public int allfails;
  
  public StatsDna() {
    super("stats_dna");
  }
  
  public DataAccess getInstance() {
    return new StatsDna();
  }
}