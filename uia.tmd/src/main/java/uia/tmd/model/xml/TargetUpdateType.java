//
// 此檔案是由 JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 所產生 
// 請參閱 <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// 一旦重新編譯來源綱要, 對此檔案所做的任何修改都將會遺失. 
// 產生時間: 2015.11.16 於 06:50:25 PM CST 
//


package uia.tmd.model.xml;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>TargetUpdateType complex type 的 Java 類別.
 * 
 * <p>下列綱要片段會指定此類別中包含的預期內容.
 * 
 * <pre>
 * &lt;complexType name="TargetUpdateType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="columns">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="column" type="{http://tmd.uia/model/xml}ColumnType" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="where">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="column" type="{http://tmd.uia/model/xml}ColumnType" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="table" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TargetUpdateType", propOrder = {
    "description",
    "columns",
    "where"
})
public class TargetUpdateType {

    @XmlElement(required = true)
    protected String description;
    @XmlElement(required = true)
    protected TargetUpdateType.Columns columns;
    @XmlElement(required = true)
    protected TargetUpdateType.Where where;
    @XmlAttribute(name = "table")
    protected String table;

    /**
     * 取得 description 特性的值.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * 設定 description 特性的值.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * 取得 columns 特性的值.
     * 
     * @return
     *     possible object is
     *     {@link TargetUpdateType.Columns }
     *     
     */
    public TargetUpdateType.Columns getColumns() {
        return columns;
    }

    /**
     * 設定 columns 特性的值.
     * 
     * @param value
     *     allowed object is
     *     {@link TargetUpdateType.Columns }
     *     
     */
    public void setColumns(TargetUpdateType.Columns value) {
        this.columns = value;
    }

    /**
     * 取得 where 特性的值.
     * 
     * @return
     *     possible object is
     *     {@link TargetUpdateType.Where }
     *     
     */
    public TargetUpdateType.Where getWhere() {
        return where;
    }

    /**
     * 設定 where 特性的值.
     * 
     * @param value
     *     allowed object is
     *     {@link TargetUpdateType.Where }
     *     
     */
    public void setWhere(TargetUpdateType.Where value) {
        this.where = value;
    }

    /**
     * 取得 table 特性的值.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTable() {
        return table;
    }

    /**
     * 設定 table 特性的值.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTable(String value) {
        this.table = value;
    }


    /**
     * <p>anonymous complex type 的 Java 類別.
     * 
     * <p>下列綱要片段會指定此類別中包含的預期內容.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="column" type="{http://tmd.uia/model/xml}ColumnType" maxOccurs="unbounded"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "column"
    })
    public static class Columns {

        @XmlElement(required = true)
        protected List<ColumnType> column;

        /**
         * Gets the value of the column property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the column property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getColumn().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link ColumnType }
         * 
         * 
         */
        public List<ColumnType> getColumn() {
            if (column == null) {
                column = new ArrayList<ColumnType>();
            }
            return this.column;
        }

    }


    /**
     * <p>anonymous complex type 的 Java 類別.
     * 
     * <p>下列綱要片段會指定此類別中包含的預期內容.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="column" type="{http://tmd.uia/model/xml}ColumnType" maxOccurs="unbounded"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "column"
    })
    public static class Where {

        @XmlElement(required = true)
        protected List<ColumnType> column;

        /**
         * Gets the value of the column property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the column property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getColumn().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link ColumnType }
         * 
         * 
         */
        public List<ColumnType> getColumn() {
            if (column == null) {
                column = new ArrayList<ColumnType>();
            }
            return this.column;
        }

    }

}
