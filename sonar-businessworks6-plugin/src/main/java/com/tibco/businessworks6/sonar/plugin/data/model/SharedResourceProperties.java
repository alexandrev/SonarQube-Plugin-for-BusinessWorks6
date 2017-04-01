
package com.tibco.businessworks6.sonar.plugin.data.model;

import java.util.ArrayList;
import java.util.List;

/** 
 * Comment describing your root element
 * 
 * Schema fragment(s) for this class:
 * <pre>
 * &lt;xs:element xmlns:xs="http://www.w3.org/2001/XMLSchema" name="SharedResourceProperties">
 *   &lt;xs:complexType>
 *     &lt;xs:sequence>
 *       &lt;xs:element name="SharedResource" maxOccurs="unbounded">
 *         &lt;!-- Reference to inner class SharedResource -->
 *       &lt;/xs:element>
 *     &lt;/xs:sequence>
 *   &lt;/xs:complexType>
 * &lt;/xs:element>
 * </pre>
 */
public class SharedResourceProperties
{
    private List<SharedResource> sharedResourceList = new ArrayList<SharedResource>();

    /** 
     * Get the list of 'SharedResource' element items.
     * 
     * @return list
     */
    public List<SharedResource> getSharedResourceList() {
        return sharedResourceList;
    }

    /** 
     * Set the list of 'SharedResource' element items.
     * 
     * @param list
     */
    public void setSharedResourceList(List<SharedResource> list) {
        sharedResourceList = list;
    }
    /** 
     * Schema fragment(s) for this class:
     * <pre>
     * &lt;xs:element xmlns:xs="http://www.w3.org/2001/XMLSchema" name="SharedResource" maxOccurs="unbounded">
     *   &lt;xs:complexType>
     *     &lt;xs:sequence>
     *       &lt;xs:element name="Property" maxOccurs="unbounded">
     *         &lt;!-- Reference to inner class Property -->
     *       &lt;/xs:element>
     *     &lt;/xs:sequence>
     *     &lt;xs:attribute type="xs:string" name="name"/>
     *     &lt;xs:attribute type="xs:string" name="type"/>
     *   &lt;/xs:complexType>
     * &lt;/xs:element>
     * </pre>
     */
    public static class SharedResource
    {
        private List<Property> propertyList = new ArrayList<Property>();
        private String name;
        private String type;

        /** 
         * Get the list of 'Property' element items.
         * 
         * @return list
         */
        public List<Property> getPropertyList() {
            return propertyList;
        }

        /** 
         * Set the list of 'Property' element items.
         * 
         * @param list
         */
        public void setPropertyList(List<Property> list) {
            propertyList = list;
        }

        /** 
         * Get the 'name' attribute value.
         * 
         * @return value
         */
        public String getName() {
            return name;
        }

        /** 
         * Set the 'name' attribute value.
         * 
         * @param name
         */
        public void setName(String name) {
            this.name = name;
        }

        /** 
         * Get the 'type' attribute value.
         * 
         * @return value
         */
        public String getType() {
            return type;
        }

        /** 
         * Set the 'type' attribute value.
         * 
         * @param type
         */
        public void setType(String type) {
            this.type = type;
        }
        /** 
         * Schema fragment(s) for this class:
         * <pre>
         * &lt;xs:element xmlns:xs="http://www.w3.org/2001/XMLSchema" name="Property" maxOccurs="unbounded">
         *   &lt;xs:complexType>
         *     &lt;xs:sequence>
         *       &lt;xs:element name="Dependency" maxOccurs="unbounded">
         *         &lt;!-- Reference to inner class Dependency -->
         *       &lt;/xs:element>
         *     &lt;/xs:sequence>
         *     &lt;xs:attribute type="xs:string" name="name"/>
         *     &lt;xs:attribute type="xs:boolean" name="required"/>
         *     &lt;xs:attribute type="xs:boolean" name="binding"/>
         *   &lt;/xs:complexType>
         * &lt;/xs:element>
         * </pre>
         */
        public static class Property
        {
            private List<Dependency> dependencyList = new ArrayList<Dependency>();
            private String name;
            private Boolean required;
            private Boolean binding;

            /** 
             * Get the list of 'Dependency' element items.
             * 
             * @return list
             */
            public List<Dependency> getDependencyList() {
                return dependencyList;
            }

            /** 
             * Set the list of 'Dependency' element items.
             * 
             * @param list
             */
            public void setDependencyList(List<Dependency> list) {
                dependencyList = list;
            }

            /** 
             * Get the 'name' attribute value.
             * 
             * @return value
             */
            public String getName() {
                return name;
            }

            /** 
             * Set the 'name' attribute value.
             * 
             * @param name
             */
            public void setName(String name) {
                this.name = name;
            }

            /** 
             * Get the 'required' attribute value.
             * 
             * @return value
             */
            public Boolean getRequired() {
                return required;
            }

            /** 
             * Set the 'required' attribute value.
             * 
             * @param required
             */
            public void setRequired(Boolean required) {
                this.required = required;
            }

            /** 
             * Get the 'binding' attribute value.
             * 
             * @return value
             */
            public Boolean getBinding() {
                return binding;
            }

            /** 
             * Set the 'binding' attribute value.
             * 
             * @param binding
             */
            public void setBinding(Boolean binding) {
                this.binding = binding;
            }
            /** 
             * Schema fragment(s) for this class:
             * <pre>
             * &lt;xs:element xmlns:xs="http://www.w3.org/2001/XMLSchema" name="Dependency" maxOccurs="unbounded">
             *   &lt;xs:complexType>
             *     &lt;xs:attribute type="xs:string" name="field"/>
             *     &lt;xs:attribute type="xs:string" name="value"/>
             *   &lt;/xs:complexType>
             * &lt;/xs:element>
             * </pre>
             */
            public static class Dependency
            {
                private String field;
                private String value;

                /** 
                 * Get the 'field' attribute value.
                 * 
                 * @return value
                 */
                public String getField() {
                    return field;
                }

                /** 
                 * Set the 'field' attribute value.
                 * 
                 * @param field
                 */
                public void setField(String field) {
                    this.field = field;
                }

                /** 
                 * Get the 'value' attribute value.
                 * 
                 * @return value
                 */
                public String getValue() {
                    return value;
                }

                /** 
                 * Set the 'value' attribute value.
                 * 
                 * @param value
                 */
                public void setValue(String value) {
                    this.value = value;
                }
            }
        }
    }
}
