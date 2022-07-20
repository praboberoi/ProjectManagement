ALTER TABLE user_roles ADD column order_column INTEGER;

UPDATE user_roles 
SET order_column = NULL;

UPDATE user_roles 
SET order_column = 0 
WHERE (User_User_Id, roles) IN (SELECT User_User_Id, MIN(roles)
	FROM User_Roles 
	WHERE order_column IS NULL
    	GROUP BY User_User_Id);

UPDATE user_roles SET order_column = 1
WHERE (User_User_Id, roles) IN (SELECT User_User_Id, MIN(roles)
	FROM User_Roles 
	WHERE order_column IS NULL
    GROUP BY User_User_Id);

UPDATE user_roles SET order_column = 2
WHERE (User_User_Id, roles) IN (SELECT User_User_Id, MIN(roles)
	FROM User_Roles 
	WHERE order_column IS NULL
    GROUP BY User_User_Id);

ALTER TABLE user_roles ALTER order_column SET NOT NULL;