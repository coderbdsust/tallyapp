package com.udayan.tallyapp.ledger;

import com.udayan.tallyapp.employee.Employee;
import com.udayan.tallyapp.model.BaseEntity;

import java.time.LocalDate;


//@EqualsAndHashCode(callSuper = true)
//@Entity
//@Table(name="general_ledger")
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
public class GeneralLedger extends BaseEntity {
    private ExpenseCategoryType expenseCategoryType;
    private Double workUnit;
    private Double workUnitBaseAmount;
    private Double expenseAmount;
    private Employee employee;
    private LocalDate transactionDate;
}
