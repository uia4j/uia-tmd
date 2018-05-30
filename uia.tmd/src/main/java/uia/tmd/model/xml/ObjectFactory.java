//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.05.29 at 02:36:02 PM CST 
//


package uia.tmd.model.xml;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the uia.tmd.model.xml package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Tmd_QNAME = new QName("http://tmd.uia/model/xml", "tmd");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: uia.tmd.model.xml
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SourceSelectType.Pk }
     * 
     */
    public SourceSelectType.Pk createSourceSelectTypePk() {
        return new SourceSelectType.Pk();
    }

    /**
     * Create an instance of {@link TmdType }
     * 
     */
    public TmdType createTmdType() {
        return new TmdType();
    }

    /**
     * Create an instance of {@link ExecutorSpaceType }
     * 
     */
    public ExecutorSpaceType createExecutorSpaceType() {
        return new ExecutorSpaceType();
    }

    /**
     * Create an instance of {@link PlanType.Join }
     * 
     */
    public PlanType.Join createPlanTypeJoin() {
        return new PlanType.Join();
    }

    /**
     * Create an instance of {@link TargetUpdateType }
     * 
     */
    public TargetUpdateType createTargetUpdateType() {
        return new TargetUpdateType();
    }

    /**
     * Create an instance of {@link PlanType.Where }
     * 
     */
    public PlanType.Where createPlanTypeWhere() {
        return new PlanType.Where();
    }

    /**
     * Create an instance of {@link TableSpaceType }
     * 
     */
    public TableSpaceType createTableSpaceType() {
        return new TableSpaceType();
    }

    /**
     * Create an instance of {@link ExecutorType }
     * 
     */
    public ExecutorType createExecutorType() {
        return new ExecutorType();
    }

    /**
     * Create an instance of {@link PlanType.Rule }
     * 
     */
    public PlanType.Rule createPlanTypeRule() {
        return new PlanType.Rule();
    }

    /**
     * Create an instance of {@link MCriteriaType }
     * 
     */
    public MCriteriaType createMCriteriaType() {
        return new MCriteriaType();
    }

    /**
     * Create an instance of {@link TaskSpaceType }
     * 
     */
    public TaskSpaceType createTaskSpaceType() {
        return new TaskSpaceType();
    }

    /**
     * Create an instance of {@link PlanType }
     * 
     */
    public PlanType createPlanType() {
        return new PlanType();
    }

    /**
     * Create an instance of {@link SourceSelectType }
     * 
     */
    public SourceSelectType createSourceSelectType() {
        return new SourceSelectType();
    }

    /**
     * Create an instance of {@link MTableType.Pk }
     * 
     */
    public MTableType.Pk createMTableTypePk() {
        return new MTableType.Pk();
    }

    /**
     * Create an instance of {@link MTableType }
     * 
     */
    public MTableType createMTableType() {
        return new MTableType();
    }

    /**
     * Create an instance of {@link TargetUpdateType.Pk }
     * 
     */
    public TargetUpdateType.Pk createTargetUpdateTypePk() {
        return new TargetUpdateType.Pk();
    }

    /**
     * Create an instance of {@link TargetUpdateType.Columns }
     * 
     */
    public TargetUpdateType.Columns createTargetUpdateTypeColumns() {
        return new TargetUpdateType.Columns();
    }

    /**
     * Create an instance of {@link DatabaseSpaceType }
     * 
     */
    public DatabaseSpaceType createDatabaseSpaceType() {
        return new DatabaseSpaceType();
    }

    /**
     * Create an instance of {@link TaskType.Next }
     * 
     */
    public TaskType.Next createTaskTypeNext() {
        return new TaskType.Next();
    }

    /**
     * Create an instance of {@link MColumnType }
     * 
     */
    public MColumnType createMColumnType() {
        return new MColumnType();
    }

    /**
     * Create an instance of {@link DbServerType }
     * 
     */
    public DbServerType createDbServerType() {
        return new DbServerType();
    }

    /**
     * Create an instance of {@link TaskType }
     * 
     */
    public TaskType createTaskType() {
        return new TaskType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TmdType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tmd.uia/model/xml", name = "tmd")
    public JAXBElement<TmdType> createTmd(TmdType value) {
        return new JAXBElement<TmdType>(_Tmd_QNAME, TmdType.class, null, value);
    }

}
