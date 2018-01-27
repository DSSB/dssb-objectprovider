package dssb.objectprovider.impl;

import static org.junit.Assert.assertNull;

import java.util.Optional;

import org.junit.Test;

import dssb.objectprovider.impl.annotations.Nullable;
import dssb.utils.common.Nulls;
import lombok.experimental.ExtensionMethod;

@ExtensionMethod({ Nulls.class })
public class ConstructorWithOptionalTest {
    
    private ObjectProvider provider = new ObjectProvider();
    
    public static interface Department {
        public String name();
    }
    
    @ExtensionMethod({ Nulls.class })
    public static class Employee {
        private Department department;
        public Employee(Optional<Department> department) {
            this.department = department.orElse(null);
        }
        public String departmentName() {
            return department.whenNotNull().map(Department::name).orElse(null);
        }
    }
    
    @Test
    public void testThat_OptionalEmptyIsGivenIfTheValueCannotBeObtained() {
        assertNull(provider.get(Employee.class).departmentName());
    }
    
    
    public static class Salary {
        public Salary() {
            throw new RuntimeException("Too much");
        }
    }
    
    @ExtensionMethod({ Nulls.class })
    public static class Executive {
        private Optional<Salary> salary;
        public Executive(@Nullable Optional<Salary> salary) {
            this.salary = salary;
        }
        public Optional<Salary> salary() {
            return salary;
        }
    }
    
    @Test
    public void testThat_nullIsGivenToNullableOptionalParameterIfTheValueCannotBeObtainedDueToException() {
        // Since Department is an interface an no default is given, so its value can't be found.
        assertNull(provider.get(Executive.class).salary());
    }
    
}
