
## mybatis model helper
- Summary
  <p>
 Simplify mybatis development. Make writing SQL like writing java code. 
 Here we call it JQL. And form a set of JQL API process to reduce the SQL error rate.
 JQL is designed to decompose complex SQL into simple SQL, 
 so JQL is designed to support up to two table associated queries.
 We do not recommend joining more than 2 tables. 
 This reduces the readability and maintainability of SQL.
 And new writing formats are supported in modelhelper.
 The default method can be written in the java interface,
 and the JQL API can be directly manipulated internally (provided that crudmapper is inherited). 
 Let me write JQL in Java stream to improve development efficiency. Less code and smoother writing.
</p>
 
- maven
  ```java
    <!-- https://mvnrepository.com/artifact/com.javaoffers/mybatis-model-spring-support/3.5.11.3 -->
   <dependency>
      <groupId>com.javaoffers</groupId>
      <artifactId>mybatis-model-spring-support</artifactId>
      <version>3.5.11.3</version>
   </dependency>

  ```
    
- Basic Usage Tutorial 
 <p>
    A Normal Query
 </p>
 <p>
    Before looking at the operation, let's take a look at the data structure: there are two key annotations here.
     @BaseModel is used to indicate that the class belongs to the model class (the class name is the same as the table name,
      ModelHelp will eventually convert the camel case class name into an underscore table name, and the attributes are the same), 
      @BaseUnique indicates the only attribute in the class (corresponding to A unique attribute in a table, which can be multiple 
      when a federated primary key is used in the table). We will explain the use of annotations in detail at the end. Here is the basic use
 </p>
 
 ```java
@BaseModel
public class User {

    @BaseUnique
    private Long id;

    private String name;

    private String birthday;
    // .... getter setter
}
```

 ```java

 List<User>  users = crudUserMapper 
    .select() 
    .colAll() 
    .where() 
    .exs(); 
 ```
 
  <p>
This JQL will eventually be translated as select * from user. Here, colall means to query all table   fields. If you want to query the specified fields, such as the name and birthday fields, you can do this: 
 </p>
 
 ```java
 List<User> users = crudusermapper
     .select()
     .col (user:: getbirthday)
     .col (user:: getname)
     .where()
    .exs();
 ```
 
 <p>
 You can specify the field you want to query through col(). The where() here is the same as the keyword where in SQL. For example, if you want to query a user whose ID value is 1, you can write this: 
 </p>
 
 ```java
 User user = crudusermapper
 .select() 
 .colAll() 
 .where() 
 .eq(User::getId, 1) 
 .ex();
 ```
 <p>
 In these three cases, you will find that there are two special functions exs(), ex() These two functions represent trigger execution. exs() is usually used to query more data, and the returned result is list, while ex() is used to return only one result T; JQL have to pass to trigger the where and ex/exs . In most work scenarios, filter conditions will be added after WHERE, in addition to the special count all table data, this design is also a good reminder to remember to fill in the WHERE conditions, of course, if you do not need to add any WHERE conditions in order to query all table data, you can use where().ex(), where().exs()
 </p>  
 <p>
 
   More query cases：https://github.com/caomingjie-code/Mybatis-ModelHelper/blob/master/mybatis-model-sample/src/main/java/com/javaoffers/base/modelhelper/sample/spring/SpringSuportCrudUserMapperSelete.java
 </p>

<p>
  A Normal Insert Operation
</p> 

```java
Id exOne = crudUserMapper
                .insert()
                .col(User::getBirthday, new Date())
                .col(User::getName, "Jom")
                .ex();
```
<p>
    A simple insert statement that returns a wrapper class Id, which is usually the primary key of the newly inserted data. An insert operation is so simple.
    There is also a simpler way to insert data. Insert object. and supports multiple. Formation logic is optimized for batch processing. 
    For example the following case
</p>

```java
String date = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
        User h1 = User.builder().name("Jom1").birthday(date).build();
        User h2 = User.builder().name("Jom2").birthday(date).build();
        User h3 = User.builder().name("Jom3").birthday(date).build();
        List<Id> ex = crudUserMapper.insert()
                .colAll(h1, h2, h3)
                .ex();
        print(ex);
```

<p>

  We can insert the entire model object, indicating that all fields are to be queried, and the stratum is batched. Performance is very good.
  For more cases, please refer to：https://github.com/caomingjie-code/Mybatis-ModelHelper/blob/master/mybatis-model-sample/src/main/java/com/javaoffers/base/modelhelper/sample/spring/SpringSuportCrudUserMapperInsert.java
</p>

<p>
Update operation, update operation has two modes, allowing to update null value updateNull and not allowing to update null value npdateNull, please see the following case
</p>

```java
crudUserMapper
        .update().npdateNull()
                 .col(User::getBirthday, new Date())
                 //name not will update . because its npdateNull
                 .col(User::getName,null)
                 .where()
                 .eq(User::getId, id)
                 .ex();

crudUserMapper
        .update().updateNull()
                 .col(User::getBirthday, new Date())
                 //name  will update . because its updateNull
                 .col(User::getName,null)
                 .where()
                 .eq(User::getId, id)
                 .ex();

```

<p>
   This method is very useful for model object instances. For example, some properties in a User object have values ​​(not null) and some properties have no value (null), so should the properties without values ​​be updated? You can pass npdateNull (do not update) updateNull (update property is null), such as the following case
</p>

```java
    public void testUpdateUser(){
        
        User user = User.builder().name("Jom").birthday(null).build();
        //npdateNull, birday null will not be updated to the database
        crudUserMapper.update().npdateNull()
                .colAll(user)
                .where()
                .eq(User::getId,1)
                .ex();
        
        //updateNull， birday null will be updated to the database, the value of birday will be changed to null
        crudUserMapper.update().updateNull()
                .colAll(user)
                .where()
                .eq(User::getId, 1)
                .ex();
    }

```

<p>
 Through the above case, we can control the update of the field very well in the business. When I use the model class.
</p>



<p>
A new way of encoding. We can write default method in Mapper interface. For example the following case
We recommend using this style
</p>

```java
public interface CrudUserMapper extends CrudMapper<User> {

    default User queryUserById(Number id){
        return select()
                .colAll()
                .where()
                .eq(User::getId, id)
                .ex();
    }
}
```

<p>
When my interface inherits the CrudMapper interface, we can write our JQL logic in default. This avoids the traditional method of writing native SQL statements on the Mapper interface.
. For more cases, please see:https://github.com/caomingjie-code/Mybatis-ModelHelper/blob/master/mybatis-model-sample/src/main/java/com/javaoffers/base/modelhelper/sample/spring/mapper/CrudUserMapper.java
</p>

- demo crud:
  - demo ：https://github.com/caomingjie-code/Mybatis-ModelHelper/blob/master/mybatis-model-sample/src/main/java/com/javaoffers/base/modelhelper/sample/spring
    
#### Advanced Advanced
- This part mainly describes how to use JQL to express some complex query statements
<p>
   In the basic part above, we explained some common and most basic uses. Next, 
   we will introduce some scenarios in real projects. Some slightly more complex use cases. 
   It mainly includes join query, group query, statistical query, and common general operations.
</p>

<p>
    JQL provides a wealth of commonly used APIs. For example >= , <= , in , between, like,  
    likeLeft, likeRight, exists, etc. There is also a combination unite that mainly combines  
    multiple conditions into one, such as (xx > xx or xx < xx ) treats two association conditions 
    as one. At the same time, we let you write the entry of native sql, such as col(sql), 
    condSQL(sql), although we usually do not recommend using native sql. Because try not to 
    use sql for complex logic processing , such as the interception of some strings.  
    Or splicing, etc. It is recommended that these operations be handled at the business layer. 
    Let's first look at a simple join JQL case: We recommend writing JQL in an interface class
</p>

```java
public interface CrudUserMapper extends CrudMapper<User> {
    
    default List<User> queryAllAndOrder(){
        return select()
                .colAll()
                .leftJoin(UserOrder::new)
                .colAll()
                .on()
                .oeq(User::getId,UserOrder::getUserId)
                .where()
                .exs();
    }
}
```
<p>
 This JQL is to query the data that user and userOrder satisfy the relationship,And it automatically maps one-to-many relationships. The structure of the User class is as follows：
</p>

```java
 @BaseModel     
 public class User {

    @BaseUnique
    private Long id;

    private String name;

    private String birthday;

    private String createTime;

    private List<UserOrder> orders; //It will automatically map the data into it
      
    //getter, setter methods 
  }
      
 @BaseModel
 public class UserOrder {

    @BaseUnique
    private int id;
    private String orderName;
    private String orderMoney;
      
    //getter, setter  methods  
 }     
      
```   

<p>
    use left join , group by , limitPage  
</p>   
      
```java
 crudUserMapper.select()
                .col(AggTag.MAX, User::getName)
                .leftJoin(UserOrder::new)
                .col(AggTag.MAX, UserOrder::getOrderName)
                .on()
                .oeq(User::getId, UserOrder::getUserId)
                .where()
                //Group by main table
                .groupBy(User::getName, User::getId)
                //Group according to sub-table
                .groupBy(UserOrder::getUserId)
                .limitPage(1,10)
                .exs();

```

<p>
use left join , group by , having,  limitPage 
</p>

```java
crudUserMapper.select()
                .col(AggTag.MAX, User::getName)
                .leftJoin(UserOrder::new)
                .col(AggTag.MAX, UserOrder::getOrderName)
                .on()
                .oeq(User::getId, UserOrder::getUserId)
                .where()
                .groupBy(User::getName, User::getId)//Group by main table
                .groupBy(UserOrder::getUserId) //Group according to sub-table
                .having()
                //Main table statistics function
                .eq(AggTag.COUNT,User::getName,1)
                .or()
                // unite mean (xx and xx)
                .unite(unite->{
                    unite.in(AggTag.COUNT, UserOrder::getUserId,1 )
                        .in(AggTag.COUNT, UserOrder::getUserId,1);
                })
                //Subtable statistics function
                .gt(AggTag.COUNT,UserOrder::getOrderId,1)
                .limitPage(1,10)
                .exs();

```
<p>
use left join , group by  , order by, limitPage
</p>

```java
crudUserMapper.select()
                .col(AggTag.MAX, User::getName)
                .leftJoin(UserOrder::new)
                .col(AggTag.MAX, UserOrder::getOrderName)
                .on()
                .oeq(User::getId, UserOrder::getUserId)
                .where()
                //Group by main table
                .groupBy(User::getName, User::getId)
                //Group according to sub-table
                .groupBy(UserOrder::getUserId)
                // Sort by primary table
                .orderA(User::getName)
                //Sort by subtable
                .orderA(UserOrder::getUserId)
                .limitPage(1,10)
                .exs();
```

<p>
use inner join on unite, where unite  group by  , having unite, order by, limitPage
</p>

```java
crudUserMapper.select()
                .col(AggTag.MAX, User::getName)
                .innerJoin(UserOrder::new)
                .col(AggTag.MAX, UserOrder::getOrderName)
                .on()
                .oeq(User::getId, UserOrder::getUserId)
                //support and (xxx or xxx )
                .unite(unite->{
                        unite.eq(UserOrder::getUserId,1)
                            .or()
                            .eq(UserOrder::getUserId, 2);
                })
                .where()
                //support and (xxx or xxx )
                .unite(unite->{
                         unite.eq(User::getId,1)
                            .or()
                            .eq(User::getId,2);
                })
                //According to the main group
                .groupBy(User::getName, User::getId)
                //Group according to sub-table
                .groupBy(UserOrder::getUserId)
                .having()
                // Sort by primary table
                .orderA(User::getName)
                //Sort by subtable
                .orderD(UserOrder::getUserId)
                .limitPage(1,10)
                .exs();

```

####  Commonly used

<p>
    I encapsulate some commonly used functions, which are very simple to use. 
    And the code is also very concise and clear. Such as query or change by id.
</p>

<p>
    Commonly used apis only need to be used by calling the general() method. For example, query data by id
</p>

```java
//query by id
User user = crudUserMapper.general().queryById(id);
```

<p>
    save api，save an object to the database
</p>

```java
 User user = User.builder().name("general").build();
 //save
 long saveId = crudUserMapper.general().save(user);
```

<p>
    Delete the specified data by id
</p>

```java
crudUserMapper.general().removeById(1);
```

<p>
    More simple and commonly used APIs are as follows. 
</p>

```java
    /**
     * save model
     * @param model class
     * @return primary key id
     */
    public long save(T model);

    /**
     * save model
     * @param models class
     * @return primary key id
     */
    public List<Long> saveBatch(Collection<T> models);

    /**
     * delete model.Where conditions will be generated based 
        on properties of the model
     * class for which there is a value.
     * @param model
     */
    public int remove(T model);

    /**
     * delete model by id
     */
    public int removeById(Serializable id );

    /**
     * delete model by ids
     */
    public int removeByIds(Serializable... ids );

    /**
     * delete model by ids
     */
    public int removeByIds(Collection<Serializable> ids);

    /**
     * Update the model, note that the update condition is 
        the property marked with the Unique annotation.
     * Only properties with values ​​are updated.
     * In other words, the @BaseUnique annotation will generate 
        a Where condition, and other non-null properties will
     * generate a set statement
     * @param model model
     * @return The number of bars affected by the update
     */
    public int modifyById(T model);

    /**
     * batch update
     * @param models models
     * @return Affect the number of bars
     */
    public int modifyBatchById(Collection<T> models);

    /**
     * Query the main model, be careful not to include child models. 
        Non-null properties will generate a where statement.
     * <>Note that properties such as Collection<Model> will be ignored, 
        even if they are not null </>
     * @param model model
     * @return return query result
     */
    public List<T> query(T model);

    /**
     * Query the main model, be careful not to include child models. 
        Non-null properties will generate a where statement.
     * <>Note that properties such as Collection<Model> will be ignored, 
        even if they are not null </>
     * @param model model
     * @param pageNum page number
     * @param pageSize Number of bars displayed per page
     * @return return query result
     */
    public List<T> query(T model,int pageNum,int pageSize);

    /**
     * Paging query full table data
     * @param pageNum page number, If the parameter is less than 1, 
        it defaults to 1
     * @param pageSize Number of bars displayed per page， 
        If the parameter is less than 1, it defaults to 10
     * @return return query result
     */
    public List<T> query(int pageNum,int pageSize);

    /**
     * query by id
     * @param id primary key id
     * @return model
     */
    public T queryById(Serializable id);

    /**
     * query by id
     * @param ids primary key id
     * @return model
     */
    public List<T> queryByIds(Serializable... ids);

    /**
     * query by id
     * @param ids primary key id
     * @return model
     */
    public List<T> queryByIds(Collection<Serializable> ids);

    /**
     * Map<String,Object>. String: Field names of the table. 
        The value corresponding to the Object field
     * @param param Parameters. key database field name, value field value
     * @return model
     */
    public List<T> queryByParam(Map<String,Object> param);

    /**
     * Map<String,Object>. String: Field names of the table. 
        The value corresponding to the Object field
     * @param param Parameters. key database field name, value field value
     * @param pageNum page number
     * @param pageSize Number of bars displayed per page
     * @return model
     */
    public List<T> queryByParam(Map<String,Object> param,int pageNum,int pageSize);


   /**
     * The number of statistical tables
     * @return
     */
    public long count();

    /**
     * The number of statistical tables, through the specified field
     * @return
     */
    public long count(C c);
```
### Sql function annotation
<p>
    We can use sql functions by using annotations on the fields of the class. 
    Here are some use cases：
</p>

```java
public class FunAnnoParserSample {
    @ColName("name")
    @Left(10)
    private String colName1; //LEFT(name,10)

    @ColName("name")
    @Left(10)
    @Concat( {"age"})
    private String colName2; //CONCAT(LEFT(name,10),age)


    @Left(10)
    @Concat( {"age"})
    private String colName3;//CONCAT(LEFT(colName3,10),age)

    @Now
    @Left(10)
    @Concat( {"age"})
    private String colName4;//CONCAT(LEFT(NOW(),10),age)


    @Concat( {"age"})
    private String colName5;//CONCAT(colName5,age)

    @Now
    @Concat({"age"})
    @Left(10)
    private String colName6;//LEFT(CONCAT(NOW(),age),10)


    @Concat({"age"})
    @Left(10)
    private String colName7;//LEFT(CONCAT(colName7,age),10)

    @Now
    @Left(10)
    private String colName8;//LEFT(NOW(),10)


    @Rand
    private String colName9;//RAND()

    @Rand
    @ColName("name")
    private String colName10;//java.lang.IllegalArgumentException: @ColName and @RAND cannot be used together

    @ColName("name")
    @IfNull("'Amop'")
    private String colName11;//IFNULL(name,'Amop')

    @ColName("sex = 1")
    @If(ep1 = "'boy'",ep2 = "'girl'")
    private String colName12;//IF(sex = 1,'boy','girl')

    /**
     * select if(1,'1','0') output 1
     * select if(0,'1','0') output 0
     */
    @ColName("sex")
    @IfNull("1")
    @If(ep1 = "'boy'", ep2 = "'girl'")
    private String colName13;// IF(IFNULL(sex,1),'boy','girl')

    @ColName("sex")
    @IfEq(eq = "1",ep1 = "'boy'", ep2 = "'girl'")
    private String colName14; //IF(sex = 1,'boy','girl')

    @ColName("money")
    @IfNotNull("'rich'")
    private String colName15; // IF(money is not null ,'rich',null)

    @ColName("money")
    @IfNotNull(value = "'rich'",ifNull = "'poor'")
    private String colName16; //IF(money is not null ,'rich','poor')

    @ColName("money")
    @IfNotNull(value = "'rich'",ifNull = "'poor'")
    @IfEq(eq = "'rich'",ep1 = "'i want to marry him'", ep2 = "'i want to break up with him'")
    private String colName17; //IF(IF(money is not null ,'rich','poor') = 'rich','i want to marry him','i want to break up with him')

    @ColName("money")
    @IfGt(gt = "100000",ep1 = "'rich'", ep2 = "'poor'")
    @IfEq(eq = "'rich'",ep1 = "'i want to marry him'", ep2 = "'i want to break up with him'")
    private String colName18; //IF(IF(money > 100000,'rich','poor') = 'rich','i want to marry him','i want to break up with him')

    @ColName("name")
    @Trim
    private String colName19; //TRIM(name)
}
```
### Powerful type converter
<p>
Built a large number of commonly used type converter.
Such as a database field birthday is a datetime/int,
</p>

```
   String2DoubleConvert  
    DateOne2DateTwoConvert  
    String2DateConvert  
    Boolean2StringConvert  
    Date2OffsetDateTimeConvert  
    Date2LongConvert  
    Number2SQLDateConvert  
    String2ByteConvert  
    ByteArray2StringConvert2  
    Number2DateConvert  
    Date2LocalDateTimeConvert  
    String2LocalDateConvert  
    String2OffsetDateTimeConvert  
    Number2StringConvert  
    String2FloatConvert  
    Date2StringConvert  
    String2BooleanConvert  
    String2ShortConvert  
    PrimitiveNumber2PrimitiveNumberConvert  
    String2LongConvert  
    LocalDate2StringConvert  
    String2CharConvert  
    Character2StringConvert  
    String2IntegerConvert  
    Number2LocalDateConvert  
    Number2PrimitiveConvert  
    String2LocalDateTimeConvert  
    Date2LocalDateConvert  
    String2SQLDateConvert  
    ByteArray2StringConvert  
    String2BigDecimalConvert  
    Number2BooleanConvert  
    String2BigIntegerConvert  
    Number2LocalDateTimeConvert  
```

#### Code contributions are welcome
<p>
The project is already in use internally. Development efficiency and code cleanliness have been greatly improved.
If you think it's good, please click the little star to encourage it
</p>