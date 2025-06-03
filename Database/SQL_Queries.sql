
-- CLIENT RELATED QUERIES
-- Retrieve all clients with their account details (Simple Join)
SELECT 
    C.ClientID, 
    C.first_name, 
    C.last_name, 
    A.AccountNumber, 
    A.AccountType, 
    A.Balance, 
    A.Status
FROM 
    CLIENT C
JOIN 
    ACCOUNT A ON C.ClientID = A.ClientID;

-- Count the number of accounts for each client (Aggregate with GROUP BY)
SELECT 
    C.ClientID, 
    C.first_name, 
    C.last_name, 
    COUNT(A.AccountNumber) AS NumberOfAccounts
FROM 
    CLIENT C
LEFT JOIN 
    ACCOUNT A ON C.ClientID = A.ClientID
GROUP BY 
    C.ClientID, C.first_name, C.last_name;

-- Find clients who have not taken any loans (Subquery with NOT IN)
SELECT 
    C.ClientID, 
    C.first_name, 
    C.last_name
FROM 
    CLIENT C
WHERE 
    C.ClientID NOT IN (
        SELECT 
            LH.ClientID 
        FROM 
            LOAN_HOLDER LH
    );

-- Retrieve clients who have both a savings account and a loan (Complex Join with Subquery)
SELECT 
    C.ClientID, 
    C.first_name, 
    C.last_name
FROM 
    CLIENT C
WHERE 
    C.ClientID IN (
        SELECT 
            A.ClientID 
        FROM 
            ACCOUNT A 
        WHERE 
            A.AccountType = 'Savings'
    )
AND 
    C.ClientID IN (
        SELECT 
            LH.ClientID 
        FROM 
            LOAN_HOLDER LH
    );


-- ACCOUNT RELATED QUERIES
-- Find the total number of active, closed, and suspended accounts (Aggregate with GROUP BY)
SELECT 
    Status, 
    COUNT(AccountNumber) AS NumberOfAccounts
FROM 
    ACCOUNT
GROUP BY 
    Status;

-- Count the total number of accounts in the bank
SELECT 
    COUNT(AccountNumber) AS TotalAccount
FROM 
    ACCOUNT;

-- list of all accounts 
SELECT 
    a.AccountNumber, 
    a.AccountType, 
    a.status, 
    a.CreatedAt AS Date_of_Creation, 
    CONCAT(s.first_name, ' ', s.last_name, '(ID: ', s.StaffID, ')') AS Created_by,
    CONCAT(c.first_name, ' ', c.last_name, '(ID: ',c.ClientID, ')') AS Owner
FROM 
    ACCOUNT a
JOIN 
    STAFF s ON a.CreatedBy = s.StaffID
JOIN 
    CLIENT c ON a.ClientID = c.ClientID;


-- TRANSACTIONS RELATED QUERIES
-- Retrieve all transactions for a specific account (Filtered Join)
SELECT 
    T.TransactionID, 
    T.TransactionType, 
    T.Amount, 
    T.DateTime, 
    T.SourceAccount, 
    T.DestinationAccount
FROM 
    TRANSACTION T
JOIN 
    ACCOUNT A ON T.SourceAccount = A.AccountNumber
WHERE 
    A.AccountNumber = 'ACC1001'; -- Replace with actual account number

-- Find the total number of transactions per account (Aggregate with GROUP BY and JOIN)
SELECT 
    A.AccountNumber, 
    COUNT(T.TransactionID) AS NumberOfTransactions
FROM 
    ACCOUNT A
LEFT JOIN 
    TRANSACTION T ON A.AccountNumber = T.SourceAccount
GROUP BY 
    A.AccountNumber;
    
-- past transaction by a client
SELECT 
    T.TransactionID,
    T.TransactionType,
    T.Amount,
    T.DateTime,
    T.SourceAccount,
    T.DestinationAccount
FROM 
    TRANSACTION T
JOIN 
    ACCOUNT A ON (T.SourceAccount = A.AccountNumber OR T.DestinationAccount = A.AccountNumber)
WHERE 
    A.ClientID = 1
ORDER BY 
    T.DateTime DESC;


-- LOAN RELATED QUERIES
--  Find clients who have taken loans and their loan details (Join with multiple tables)
SELECT 
    C.ClientID, 
    C.first_name, 
    C.last_name, 
    L.LoanNumber, 
    L.Amount, 
    L.LoanType, 
    L.LoanStatus
FROM 
    CLIENT C
JOIN 
    LOAN_HOLDER LH ON C.ClientID = LH.ClientID
JOIN 
    LOAN L ON LH.LoanNumber = L.LoanNumber;

-- Find the average loan amount for each loan type (Aggregate with GROUP BY)
SELECT 
    LoanType, 
    AVG(Amount) AS AverageLoanAmount
FROM 
    LOAN
GROUP BY 
    LoanType;

-- Retrieve the total amount of payments made for each loan (Aggregate with GROUP BY and JOIN)
SELECT 
    L.LoanNumber, 
    SUM(LP.PaidAmount) AS TotalPaidAmount
FROM 
    LOAN L
JOIN 
    LOANPAYMENT LP ON L.LoanNumber = LP.LoanNumber
GROUP BY 
    L.LoanNumber;
    
-- loans taken by a client
SELECT 
    L.LoanNumber, L.Amount, L.LoanType, L.LoanStatus
FROM 
    LOAN L
INNER JOIN 
    LOAN_HOLDER LH ON L.LoanNumber = LH.LoanNumber
WHERE 
    LH.ClientID = 12;


-- STAFF RELATED QUERIES
-- Retrieve the total amount of loans approved by each staff member (Aggregate with GROUP BY and JOIN)
SELECT 
    S.StaffID, 
    S.first_name, 
    S.last_name, 
    SUM(L.Amount) AS TotalLoansApproved
FROM 
    STAFF S
JOIN 
    LOAN_HOLDER LH ON S.StaffID = LH.StaffID
JOIN 
    LOAN L ON LH.LoanNumber = L.LoanNumber
GROUP BY 
    S.StaffID, S.first_name, S.last_name;

-- Retrieve the staff member who has approved the most loans (Subquery with ORDER BY and LIMIT)
SELECT 
    S.StaffID, 
    S.first_name, 
    S.last_name, 
    COUNT(LH.LoanNumber) AS NumberOfLoansApproved
FROM 
    STAFF S
JOIN 
    LOAN_HOLDER LH ON S.StaffID = LH.StaffID
GROUP BY 
    S.StaffID, S.first_name, S.last_name
ORDER BY 
    NumberOfLoansApproved DESC
LIMIT 1;


--  list of all accounts created by a staff member
SELECT 
    A.AccountNumber,
    A.AccountType,
    A.Balance,
    A.ClientID,
    A.CreatedAt,
    CONCAT(S.first_name, ' ', S.last_name, '(ID: ', s.StaffID, ')') AS Staff_Name_and_ID
FROM 
    ACCOUNT A
JOIN 
    STAFF S ON A.CreatedBy = S.StaffID
WHERE 
    A.CreatedBy = 2; -- Replace 5 with the desired Staff ID
    
-- list of all staff members with their position
SELECT 
    StaffID, 
    CONCAT(first_name, ' ', last_name) AS Name,
    Email,
    Role
FROM STAFF;








