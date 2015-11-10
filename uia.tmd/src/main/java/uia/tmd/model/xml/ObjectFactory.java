//
// 此檔案是由 JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 所產生 
// 請參閱 <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// 一旦重新編譯來源綱要, 對此檔案所做的任何修改都將會遺失. 
// 產生時間: 2015.11.10 於 08:18:17 PM CST 
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
     * Create an instance of {@link TargetInsertType }
     * 
     */
    public TargetInsertType createTargetInsertType() {
        return new TargetInsertType();
    }

    /**
     * Create an instance of {@link SourceSelectType }
     * 
     */
    public SourceSelectType createSourceSelectType() {
        return new SourceSelectType();
    }

    /**
     * Create an instance of {@link PlanType }
     * 
     */
    public PlanType createPlanType() {
        return new PlanType();
    }

    /**
     * Create an instance of {@link TaskType }
     * 
     */
    public TaskType createTaskType() {
        return new TaskType();
    }

    /**
     * Create an instance of {@link TmdType }
     * 
     */
    public TmdType createTmdType() {
        return new TmdType();
    }

    /**
     * Create an instance of {@link WhereType }
     * 
     */
    public WhereType createWhereType() {
        return new WhereType();
    }

    /**
     * Create an instance of {@link DbServerType }
     * 
     */
    public DbServerType createDbServerType() {
        return new DbServerType();
    }

    /**
     * Create an instance of {@link TaskSpaceType }
     * 
     */
    public TaskSpaceType createTaskSpaceType() {
        return new TaskSpaceType();
    }

    /**
     * Create an instance of {@link DatabaseSpaceType }
     * 
     */
    public DatabaseSpaceType createDatabaseSpaceType() {
        return new DatabaseSpaceType();
    }

    /**
     * Create an instance of {@link TargetInsertType.DelWhere }
     * 
     */
    public TargetInsertType.DelWhere createTargetInsertTypeDelWhere() {
        return new TargetInsertType.DelWhere();
    }

    /**
     * Create an instance of {@link SourceSelectType.Where }
     * 
     */
    public SourceSelectType.Where createSourceSelectTypeWhere() {
        return new SourceSelectType.Where();
    }

    /**
     * Create an instance of {@link PlanType.Nexts }
     * 
     */
    public PlanType.Nexts createPlanTypeNexts() {
        return new PlanType.Nexts();
    }

    /**
     * Create an instance of {@link TaskType.Nexts }
     * 
     */
    public TaskType.Nexts createTaskTypeNexts() {
        return new TaskType.Nexts();
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
