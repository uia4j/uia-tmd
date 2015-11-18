//
// 此檔案是由 JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 所產生 
// 請參閱 <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// 一旦重新編譯來源綱要, 對此檔案所做的任何修改都將會遺失. 
// 產生時間: 2015.11.17 於 05:53:15 PM CST 
//


package uia.tmd.model.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>TmdType complex type 的 Java 類別.
 * 
 * <p>下列綱要片段會指定此類別中包含的預期內容.
 * 
 * <pre>
 * &lt;complexType name="TmdType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="taskSpace" type="{http://tmd.uia/model/xml}TaskSpaceType"/>
 *         &lt;element name="tableSpace" type="{http://tmd.uia/model/xml}TableSpaceType"/>
 *         &lt;element name="databaseSpace" type="{http://tmd.uia/model/xml}DatabaseSpaceType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TmdType", propOrder = {
    "taskSpace",
    "tableSpace",
    "databaseSpace"
})
public class TmdType {

    @XmlElement(required = true)
    protected TaskSpaceType taskSpace;
    @XmlElement(required = true)
    protected TableSpaceType tableSpace;
    @XmlElement(required = true)
    protected DatabaseSpaceType databaseSpace;

    /**
     * 取得 taskSpace 特性的值.
     * 
     * @return
     *     possible object is
     *     {@link TaskSpaceType }
     *     
     */
    public TaskSpaceType getTaskSpace() {
        return taskSpace;
    }

    /**
     * 設定 taskSpace 特性的值.
     * 
     * @param value
     *     allowed object is
     *     {@link TaskSpaceType }
     *     
     */
    public void setTaskSpace(TaskSpaceType value) {
        this.taskSpace = value;
    }

    /**
     * 取得 tableSpace 特性的值.
     * 
     * @return
     *     possible object is
     *     {@link TableSpaceType }
     *     
     */
    public TableSpaceType getTableSpace() {
        return tableSpace;
    }

    /**
     * 設定 tableSpace 特性的值.
     * 
     * @param value
     *     allowed object is
     *     {@link TableSpaceType }
     *     
     */
    public void setTableSpace(TableSpaceType value) {
        this.tableSpace = value;
    }

    /**
     * 取得 databaseSpace 特性的值.
     * 
     * @return
     *     possible object is
     *     {@link DatabaseSpaceType }
     *     
     */
    public DatabaseSpaceType getDatabaseSpace() {
        return databaseSpace;
    }

    /**
     * 設定 databaseSpace 特性的值.
     * 
     * @param value
     *     allowed object is
     *     {@link DatabaseSpaceType }
     *     
     */
    public void setDatabaseSpace(DatabaseSpaceType value) {
        this.databaseSpace = value;
    }

}
