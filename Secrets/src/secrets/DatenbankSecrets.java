
package secrets;
import net.canarymod.database.Column;
import net.canarymod.database.Column.DataType;
import net.canarymod.database.DataAccess;

public class DatenbankSecrets extends DataAccess {
  @Column(columnName = "player_name",
          columnType = Column.ColumnType.PRIMARY,
          dataType   = DataType.STRING)
  public String player_name;
  
  @Column(columnName = "s1", dataType = DataType.BOOLEAN)
  public boolean bigmush;
  
  @Column(columnName = "s2", dataType = DataType.BOOLEAN)
  public boolean hogwarts;

  @Column(columnName = "s3", dataType = DataType.BOOLEAN)
  public boolean rechtsdna;

  @Column(columnName = "s4", dataType = DataType.BOOLEAN)
  public boolean linksdna;

  @Column(columnName = "s5", dataType = DataType.BOOLEAN)
  public boolean umbrella;

  @Column(columnName = "s6", dataType = DataType.BOOLEAN)
  public boolean honigtopf;

  @Column(columnName = "s7", dataType = DataType.BOOLEAN)
  public boolean s7;

  @Column(columnName = "s8", dataType = DataType.BOOLEAN)
  public boolean s8;

  @Column(columnName = "s9", dataType = DataType.BOOLEAN)
  public boolean s9;

  @Column(columnName = "s10", dataType = DataType.BOOLEAN)
  public boolean s10;

  @Column(columnName = "s11", dataType = DataType.BOOLEAN)
  public boolean s11;

  @Column(columnName = "s12", dataType = DataType.BOOLEAN)
  public boolean s12;

  @Column(columnName = "s13", dataType = DataType.BOOLEAN)
  public boolean s13;

  @Column(columnName = "s14", dataType = DataType.BOOLEAN)
  public boolean s14;
  
  @Column(columnName = "s15", dataType = DataType.BOOLEAN)
  public boolean s15;
  
  @Column(columnName = "s16", dataType = DataType.BOOLEAN)
  public boolean s16;
  
  @Column(columnName = "s17", dataType = DataType.BOOLEAN)
  public boolean s17;
  
  @Column(columnName = "s18", dataType = DataType.BOOLEAN)
  public boolean s18;
  
  @Column(columnName = "s19", dataType = DataType.BOOLEAN)
  public boolean s19;
  
  @Column(columnName = "s20", dataType = DataType.BOOLEAN)
  public boolean s20;
  
  @Column(columnName = "s21", dataType = DataType.BOOLEAN)
  public boolean s21;
  
  @Column(columnName = "s22", dataType = DataType.BOOLEAN)
  public boolean s22;

  @Column(columnName = "s23", dataType = DataType.BOOLEAN)
  public boolean s23;
    
  @Column(columnName = "s24", dataType = DataType.BOOLEAN)
  public boolean s24;
  
  @Column(columnName = "s25", dataType = DataType.BOOLEAN)
  public boolean s25;
  
  @Column(columnName = "s26", dataType = DataType.BOOLEAN)
  public boolean s26;

  @Column(columnName = "s27", dataType = DataType.BOOLEAN)
  public boolean s27;

  public DatenbankSecrets() {
    super("datenbank_secrets");
  }
  
  public DataAccess getInstance() { 
    return new DatenbankSecrets();
  }
}

