<?php

class BackupDB
{
    private $dbHost;
    private $dbPort;
    private $dbUser;
    private $dbPassword;
    private $dbName;

    public function __construct()
    {
        $this->dbHost = getenv('MYSQL_IP');
        $this->dbPort = getenv('MYSQL_PORT');
        $this->dbUser = getenv('MYSQL_USER');
        $this->dbPassword = getenv('MYSQL_PASSWORD');
        $this->dbName = getenv('MYSQL_DB_NAME');
    }

    public function sendMail($payload)
    {
        $ch = curl_init();
        curl_setopt($ch, CURLOPT_URL, getenv('MAIL_API_URL'));
        curl_setopt($ch, CURLOPT_POST, 1);
        curl_setopt($ch, CURLOPT_HTTPHEADER, ["Content-Type: application/json"]);
        curl_setopt($ch, CURLOPT_CONNECTTIMEOUT_MS, 30000);
        curl_setopt($ch, CURLOPT_TIMEOUT_MS, 30000);
        curl_setopt($ch, CURLINFO_HEADER_OUT, true);
        curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($payload, JSON_HEX_QUOT | JSON_HEX_APOS | JSON_UNESCAPED_SLASHES | JSON_UNESCAPED_UNICODE));
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);

        $result = curl_exec($ch);
        $status = curl_getinfo($ch, CURLINFO_HTTP_CODE);
        curl_close($ch);

        if($status != 200)
        {
            echo "\nError while sending mail. Response: " . $result . ". Payload: " . json_encode($payload) . "\n";
        }
        return $result;
    }

    public function getTables()
    {
        $tables = [];
        $query = 'SHOW TABLES';
        $result = $this->runQuery($query);

        if(!empty($result))
        {
            $tables = array_column($result->fetch_all(MYSQLI_ASSOC), 'Tables_in_duel_links');
            $result->free();
        }

        return $tables;
    }

    public function runQuery($query)
    {
        $result = false;
        $dbLink = $this->connect();
        if($dbLink)
        {
            $result = mysqli_query($dbLink, $query);
            if(!$result)
            {
                echo("\nError executing mysql query.\nQuery : " . $query . ".\nResponse : " . json_encode($result, JSON_PRETTY_PRINT) . "\nError : " . $dbLink->error);
            }

            $dbLink->close();
        }
        else
        {
            Logger::error('mysql_log', "Error executing mysql query.\nEnv : " . $env . "\nDatabase : " . $databaseName . "\nQuery : " . $query . ".\nResponse : " . json_encode($result, JSON_PRETTY_PRINT) . "\nError : Couldn't connect to DB");
        }
        return $result;
    }

    public function connect()
    {
        return mysqli_connect($this->dbHost, $this->dbUser, $this->dbPassword, $this->dbName, $this->dbPort);
    }

    public function getTableStructure($tableName)
    {
        $structure = '';
        $query = 'SHOW CREATE TABLE `' . $tableName . '`';
        $result = $this->runQuery($query);
        if(!empty($result))
        {
            $rows = $result->fetch_all(MYSQLI_ASSOC);
            $result->free();

            if(!empty($rows))
            {
                $structure = $rows[0]['Create Table'];
            }
        }

        return $structure;
    }

    public function getRowCount($tableName)
    {
        $count = 0;

        $query = 'SELECT COUNT(*) as count FROM `' . $tableName . '`';
        $result = $this->runQuery($query);

        if(!empty($result))
        {
            $rows = $result->fetch_all(MYSQLI_ASSOC);
            $result->free();

            if(!empty($rows))
            {
                $row = $rows[0];
                $count = $row['count'];
            }
        }

        return $count;
    }

    public function getRows($tableName, $offset, $limit)
    {
        $rows = [];

        $query = 'SELECT * FROM `' . $tableName . '` LIMIT ' . $limit . ' OFFSET ' . $offset;
        $result = $this->runQuery($query);
        if(!empty($result))
        {
            $rows = array_merge($rows, $result->fetch_all(MYSQLI_ASSOC));
            $result->free();
        }

        return $rows;
    }



    public function execute()
    {
        $tables = $this->getTables();

        $queries = [
            "# ------------------------------------------------------------\n"
        ];

        foreach($tables as $tableName)
        {

            $queries[] = "# Dump of table " . $tableName;
            $queries[] = "# ------------------------------------------------------------";

            $structure = $this->getTableStructure($tableName);
            $queries[] = $structure . ";\n";
            $queries[] = 'TRUNCATE TABLE `' . $tableName . "`;\n";

            $totalCount = $this->getRowCount($tableName);

            $offset = 0;
            $limit = 1000;

            while($offset < $totalCount)
            {

                $rows = $this->getRows($tableName, $offset, $limit);
                if(!empty($rows))
                {
                    $columns = array_keys($rows[0]);
                    $columns = array_map(function($value) {
                        return '`' . $value . '`';
                    }, $columns);

                    $valueStrings = [];
                    foreach($rows as $row)
                    {
                        $valueString = "(";
                        $values = array_values($row);
                        $values = array_map(function($value) {
                            return '"' . str_replace('"', '\"', $value) . '"';
                        }, $values);

                        $valueString .= implode(", ", $values);

                        $valueString .= ")";
                        $valueStrings[] = $valueString;
                    }
                    $query = 'INSERT INTO `' . $tableName . '` (' . implode(', ', $columns) . ") VALUES \n" . implode(",\n", $valueStrings) . ";\n";
                    $queries[] = $query;
                }

                $offset += $limit;
            }
        }
        $queries[] = "# ------------------------------------------------------------";


        $fileName = $this->dbName . '.sql';
        file_put_contents($fileName, implode("\n", $queries));


        $payload = [
            'from' => getenv('EMAIL_FROM'),
            'to' => getenv('EMAIL_TO'),
            'subject' => 'DB Backup - ' . $this->dbName . ' ' . date('d-m-Y'),
            'body' => 'PFA',
            'attachments' => [
                [
                'filename' => $this->dbName . '.sql',
                'content' => base64_encode(file_get_contents($this->dbName . '.sql'))
                ]
            ]
        ];

        echo "\nSending email\n";

        $this->sendMail($payload);

        unlink($this->dbName . '.sql');
    }
}

$runner = new BackupDB();
$runner->execute();